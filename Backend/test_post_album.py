import json
import os, time, shutil
import random
from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from pathlib import Path

MEDIA_ROOT = Path("./uploads")


def processVideo(username, albumname, filename):
    albumPath = MEDIA_ROOT / username / albumname
    filePath = albumPath / filename
    # extract photos from videos
    os.system('python3 ./app/utils/extract.py --video {} --sampling 5 --output-root {}'.format(str(filePath), str(albumPath)))
    outputDir = filename[:-4] + '_frames'
    outputPath = albumPath / outputDir
    for f in os.listdir(outputPath):
        shutil.move(str(outputPath / f), str(albumPath / f))
    os.rmdir(outputPath)
    processImages(username, albumname)


def processImages(username, albumname):
    albumPath = MEDIA_ROOT / username / albumname
    # classify and score photos
    results = classifyAndScorePhotos(albumPath)
    for foldername in results:
        # create new folder and add to database
        folderPath = albumPath / foldername
        os.mkdir(folderPath)

        for idx, rst in enumerate(results[foldername]):
            # move file and add to database
            filename = rst['filename']
            score = rst['score']
            isRecommended = rst['isRecommended']
            newFileName = "{}_{}_{}_{}".format(username, albumname, foldername, idx)
            oldFilePath = albumPath / filename
            newFilePath = folderPath / newFileName
            shutil.move(str(oldFilePath), str(newFilePath))


"""
{
    "cats" : [{'filename': '1.jpg', 'score': 80, 'isRecommended': 0},
              {'filename': '2.jpg', 'score': 90, 'isRecommended': 1},
              ......],

    "dogs" : [{'filename': '3.jpg', 'score': 70, 'isRecommended': 0},
              {'filename': '4.jpg', 'score': 100, 'isRecommended': 1},
              ......],
    ......
}
"""
def classifyAndScorePhotos(albumPath):
    filenames = os.listdir(albumPath)
    results = {"cats" : [], "dogs" : []}
    folders = ['cats', 'dogs']
    for i, filename in enumerate(filenames):
        score = random.randint(10, 100)
        foldername = folders[0 if i < len(filenames) / 2 else 1]
        results[foldername].append({'filename': filename, 
                                    'score': score,
                                    'isRecommended': 1 if score > 60 else 0})
    return results


os.system("./bin/glisterDB reset")
processVideo('qyq', 'Animal', 'talking_dog.mp4')