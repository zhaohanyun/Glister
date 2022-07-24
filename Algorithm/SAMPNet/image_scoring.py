import requests
from torch import nn
from torchvision.models import resnet50
import torchvision.transforms as T
import torch
from app.SAMPNet.samp_net import SAMPNet
from torch.utils.data import Dataset, DataLoader
import torch
import torch.nn.functional as F
from PIL import Image
import os
import json
import torchvision.transforms as transforms
import random
import numpy as np
from app.SAMPNet.config import Config
import cv2
import matplotlib.pyplot as plt
import argparse
import operator

IMAGE_NET_MEAN = [0.485, 0.456, 0.406]
IMAGE_NET_STD = [0.229, 0.224, 0.225]

random.seed(1)
torch.manual_seed(1)
cv2.setNumThreads(0)

# %config InlineBackend.figure_format = 'retina'

torch.set_grad_enabled(False)

# COCO classes
CLASSES = [
    'N/A', 'person', 'bicycle', 'car', 'motorcycle', 'airplane', 'bus',
    'train', 'truck', 'boat', 'traffic light', 'fire hydrant', 'N/A',
    'stop sign', 'parking meter', 'bench', 'bird', 'cat', 'dog', 'horse',
    'sheep', 'cow', 'elephant', 'bear', 'zebra', 'giraffe', 'N/A', 'backpack',
    'umbrella', 'N/A', 'N/A', 'handbag', 'tie', 'suitcase', 'frisbee', 'skis',
    'snowboard', 'sports ball', 'kite', 'baseball bat', 'baseball glove',
    'skateboard', 'surfboard', 'tennis racket', 'bottle', 'N/A', 'wine glass',
    'cup', 'fork', 'knife', 'spoon', 'bowl', 'banana', 'apple', 'sandwich',
    'orange', 'broccoli', 'carrot', 'hot dog', 'pizza', 'donut', 'cake',
    'chair', 'couch', 'potted plant', 'bed', 'N/A', 'dining table', 'N/A',
    'N/A', 'toilet', 'N/A', 'tv', 'laptop', 'mouse', 'remote', 'keyboard',
    'cell phone', 'microwave', 'oven', 'toaster', 'sink', 'refrigerator', 'N/A',
    'book', 'clock', 'vase', 'scissors', 'teddy bear', 'hair drier',
    'toothbrush'
]

# colors for visualization
COLORS = [[0.000, 0.447, 0.741], [0.850, 0.325, 0.098], [0.929, 0.694, 0.125],
          [0.494, 0.184, 0.556], [0.466, 0.674, 0.188], [0.301, 0.745, 0.933]]

# standard PyTorch mean-std input image normalization
transform = T.Compose([
    T.Resize(800),
    T.ToTensor(),
    T.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
])

# for output bounding box post-processing


def box_cxcywh_to_xyxy(x):
    x_c, y_c, w, h = x.unbind(1)
    b = [(x_c - 0.5 * w), (y_c - 0.5 * h),
         (x_c + 0.5 * w), (y_c + 0.5 * h)]
    return torch.stack(b, dim=1)


def rescale_bboxes(out_bbox, size):
    img_w, img_h = size
    b = box_cxcywh_to_xyxy(out_bbox)
    b = b * torch.tensor([img_w, img_h, img_w, img_h], dtype=torch.float32)
    return b


def plot_results(pil_img, prob, boxes):
    plt.figure(figsize=(16, 10))
    plt.imshow(pil_img)
    ax = plt.gca()
    colors = COLORS * 100
    for p, (xmin, ymin, xmax, ymax), c in zip(prob, boxes.tolist(), colors):
        ax.add_patch(plt.Rectangle((xmin, ymin), xmax - xmin, ymax - ymin,
                                   fill=False, color=c, linewidth=3))
        cl = p.argmax()
        text = f'{CLASSES[cl]}: {p[cl]:0.2f}'
        ax.text(xmin, ymin, text, fontsize=15,
                bbox=dict(facecolor='yellow', alpha=0.5))
    plt.axis('off')
    plt.show()


class DETRdemo(nn.Module):
    """
    Demo DETR implementation.

    Demo implementation of DETR in minimal number of lines, with the
    following differences wrt DETR in the paper:
    * learned positional encoding (instead of sine)
    * positional encoding is passed at input (instead of attention)
    * fc bbox predictor (instead of MLP)
    The model achieves ~40 AP on COCO val5k and runs at ~28 FPS on Tesla V100.
    Only batch size 1 supported.
    """

    def __init__(self, num_classes, hidden_dim=256, nheads=8,
                 num_encoder_layers=6, num_decoder_layers=6):
        super().__init__()

        # create ResNet-50 backbone
        self.backbone = resnet50()
        del self.backbone.fc

        # create conversion layer
        self.conv = nn.Conv2d(2048, hidden_dim, 1)

        # create a default PyTorch transformer
        self.transformer = nn.Transformer(
            hidden_dim, nheads, num_encoder_layers, num_decoder_layers)

        # prediction heads, one extra class for predicting non-empty slots
        # note that in baseline DETR linear_bbox layer is 3-layer MLP
        self.linear_class = nn.Linear(hidden_dim, num_classes + 1)
        self.linear_bbox = nn.Linear(hidden_dim, 4)

        # output positional encodings (object queries)
        self.query_pos = nn.Parameter(torch.rand(100, hidden_dim))

        # spatial positional encodings
        # note that in baseline DETR we use sine positional encodings
        self.row_embed = nn.Parameter(torch.rand(50, hidden_dim // 2))
        self.col_embed = nn.Parameter(torch.rand(50, hidden_dim // 2))

    def forward(self, inputs):
        # propagate inputs through ResNet-50 up to avg-pool layer
        x = self.backbone.conv1(inputs)
        x = self.backbone.bn1(x)
        x = self.backbone.relu(x)
        x = self.backbone.maxpool(x)

        x = self.backbone.layer1(x)
        x = self.backbone.layer2(x)
        x = self.backbone.layer3(x)
        x = self.backbone.layer4(x)

        # convert from 2048 to 256 feature planes for the transformer
        h = self.conv(x)

        # construct positional encodings
        H, W = h.shape[-2:]
        pos = torch.cat([
            self.col_embed[:W].unsqueeze(0).repeat(H, 1, 1),
            self.row_embed[:H].unsqueeze(1).repeat(1, W, 1),
        ], dim=-1).flatten(0, 1).unsqueeze(1)

        # propagate through the transformer
        h = self.transformer(pos + 0.1 * h.flatten(2).permute(2, 0, 1),
                             self.query_pos.unsqueeze(1)).transpose(0, 1)

        # finally project transformer outputs to class labels and bounding boxes
        return {'pred_logits': self.linear_class(h),
                'pred_boxes': self.linear_bbox(h).sigmoid()}


def detect(im, model, transform):
    # mean-std normalize the input image (batch-size: 1)
    img = transform(im).unsqueeze(0)

    # demo model only support by default images with aspect ratio between 0.5 and 2
    # if you want to use images with an aspect ratio outside this range
    # rescale your image so that the maximum size is at most 1333 for best results
    assert img.shape[-2] <= 1600 and img.shape[-1] <= 1600, 'demo model only supports images up to 1600 pixels on each side'

    # propagate through the model
    outputs = model(img)

    # keep only predictions with 0.7+ confidence
    probas = outputs['pred_logits'].softmax(-1)[0, :, :-1]
    keep = probas.max(-1).values > 0.7

    # convert boxes from [0; 1] to image scales
    bboxes_scaled = rescale_bboxes(outputs['pred_boxes'][0, keep], im.size)
    return probas[keep], bboxes_scaled


detr = DETRdemo(num_classes=91)
state_dict = torch.hub.load_state_dict_from_url(
    url='https://dl.fbaipublicfiles.com/detr/detr_demo-da2a99e9.pth',
    map_location='cpu', check_hash=True)
detr.load_state_dict(state_dict)
detr.eval()


def plot_results(pil_img, prob, boxes, save_path=None):
    plt.figure(figsize=(16, 10))
    plt.imshow(pil_img)
    ax = plt.gca()
    for p, (xmin, ymin, xmax, ymax), c in zip(prob, boxes.tolist(), COLORS * 100):
        ax.add_patch(plt.Rectangle((xmin, ymin), xmax - xmin, ymax - ymin,
                                   fill=False, color=c, linewidth=3))
        cl = p.argmax()
        text = f'{CLASSES[cl]}: {p[cl]:0.2f}'

        ax.text(xmin, ymin, text, fontsize=15,
                bbox=dict(facecolor='yellow', alpha=0.5))
    plt.axis('off')
    if save_path is None:
        if not os.path.exists("./obj_scored_images/"):
            os.mkdir("./obj_scored_images/")
        img_num = len(os.listdir("./obj_scored_images/"))
        save_path = f"./obj_scored_images/img_{img_num}.png"
    plt.savefig(save_path)
    print(f"The scored image is saved at {save_path}")
    plt.show()


def get_obj_result(img_path=None,
                   plot=False,
                   transform=transform, ):
    if img_path is None:
        # print("==== DEMO Mode ====")
        img_path = '/root/Glister/Backend/app/SAMPNet/raw_images/000000039769.jpg'
    if img_path.startswith("http"):
        im = Image.open(requests.get(img_path, stream=True).raw)
    else:
        im = Image.open(img_path)
    scores, boxes = detect(im, detr, transform)
    obj_score_dict = {}
    objs = []
    for p, (_, _, _, _), c in zip(scores, boxes.tolist(), COLORS * 100):
        cl = p.argmax()
        obj_cls = CLASSES[cl]
        obj_score = p[cl]
        text = f'{CLASSES[cl]}: {p[cl]:0.2f}'
        obj_score_dict[obj_cls] = obj_score
        objs.append(obj_cls)

    if plot:
        print("=== image path ===")
        for i, s in obj_score_dict.items():
            print(f"{i} => {np.round(s,2)}")
        plot_results(im, scores, boxes)
    return objs

# ==================== Aesthetic Assessment ==================== #
# Refer to: Saliency detection: A spectral residual approach


def detect_saliency(img, scale=6, q_value=0.95, target_size=(224, 224)):
    img_gray = cv2.cvtColor(img, cv2.COLOR_RGB2GRAY)
    W, H = img_gray.shape
    img_resize = cv2.resize(
        img_gray, (H // scale, W // scale), interpolation=cv2.INTER_AREA)

    myFFT = np.fft.fft2(img_resize)
    myPhase = np.angle(myFFT)
    myLogAmplitude = np.log(np.abs(myFFT) + 0.000001)
    myAvg = cv2.blur(myLogAmplitude, (3, 3))
    mySpectralResidual = myLogAmplitude - myAvg

    m = np.exp(mySpectralResidual) * \
        (np.cos(myPhase) + complex(1j) * np.sin(myPhase))
    saliencyMap = np.abs(np.fft.ifft2(m)) ** 2
    saliencyMap = cv2.GaussianBlur(saliencyMap, (9, 9), 2.5)
    saliencyMap = cv2.resize(saliencyMap, target_size,
                             interpolation=cv2.INTER_LINEAR)
    threshold = np.quantile(saliencyMap.reshape(-1), q_value)
    if threshold > 0:
        saliencyMap[saliencyMap > threshold] = threshold
        saliencyMap = (saliencyMap - saliencyMap.min()) / threshold
    # for debugging
    # import matplotlib.pyplot as plt
    # plt.subplot(1,2,1)
    # plt.imshow(img)
    # plt.axis('off')
    # plt.subplot(1,2,2)
    # plt.imshow(saliencyMap, cmap='gray')
    # plt.axis('off')
    # plt.show()
    return saliencyMap


cfg = Config()
model = SAMPNet(cfg)
img_transformer = transforms.Compose([
    transforms.Resize((cfg.image_size, cfg.image_size)),
    transforms.ToTensor(),
    transforms.Normalize(mean=IMAGE_NET_MEAN, std=IMAGE_NET_STD)
])


def get_img(image_file):
    if image_file is None:
        print("==== DEMO Mode ====")
        image_file = '/root/Glister/Backend/app/SAMPNet/raw_images/000000039769.jpg'
    if image_file.startswith("http"):
        src = Image.open(requests.get(
            image_file, stream=True).raw).convert('RGB')
    else:
        src = Image.open(image_file).convert('RGB')

    im = img_transformer(src)

    src_im = np.asarray(src).copy()
    sal_map = detect_saliency(src_im, target_size=(
        cfg.image_size, cfg.image_size))
    sal_map = torch.from_numpy(sal_map.astype(np.float32)).unsqueeze(0)

    return torch.unsqueeze(im, 0), torch.unsqueeze(sal_map, 0)


def dist2ave(pred_dist):
    pred_score = torch.sum(
        pred_dist * torch.Tensor(range(1, 6)).to(pred_dist.device), dim=-1, keepdim=True)
    return pred_score

### FINAL FUNCTION ###


def score_images(image_dir_path, plot=False):
    filenames = os.listdir(image_dir_path)
    results = {}
    for filename in filenames:
        image_file_path = image_dir_path + "/" + filename
        im, sal = get_img(image_file_path)
        weight, attribute, output_score = model(im, sal)
        # if weight is not None:
        #     print('weight', weight.shape, F.softmax(weight, dim=1))
        # if attribute is not None:
        #     print('attribute', attribute.shape, attribute)
        pred_score = dist2ave(output_score)
        pred_score = np.round(pred_score.squeeze().item(), 4)

        objs = get_obj_result(image_file_path, plot)
        for obj in objs:
            if obj not in results:
                results[obj] = []
            results[obj].append({'filename': filename, 'score': int(pred_score * 20), 'isRecommended': 0})
        for rst in results.values():
            rst.sort(key=operator.itemgetter('score'))
            rst[0]['isRecommended'] = 1
    return results


#########################################################################
# Prepare Parser
##########################################################################

# if __name__ == '__main__':
#     parser = argparse.ArgumentParser()
#     parser.add_argument('--img_dir_path', required=True, default=None,
#                         help='input image path')
#     parser.add_argument('--plot', type=bool, required=False, default=False,
#                         help='whether plot the image with detected objects')
#     parser.add_argument('--rst_path', required=True, default=False,
#                         help='output file path')
#     args = parser.parse_args()
#     results = score_images(args.img_dir_path, args.plot)
#     json_object = json.dumps(results, indent=4)
#     with open(args.rst_path, "w+") as outfile:
#         outfile.write(json_object)
