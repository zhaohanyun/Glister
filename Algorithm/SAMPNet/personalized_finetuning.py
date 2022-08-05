# Retraining
import sklearn
import numpy as np
import pandas as pd
import json
from tqdm import tqdm
import pickle
from sklearn.linear_model import LinearRegression
from sklearn.svm import SVR
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

## get preprocessed CADB data 
def get_data(cade_path="/content/drive/Shareddrives/VE441/Scoring System/CADB_finetuning/processed_CADB.pkl",
             added_imgs=None, added_scores=None):
  with open(cade_path, "rb") as trg:
    img_score_dict = pickle.load(trg)
  img_list = list(img_score_dict.keys())
  score_list = list(img_score_dict.values())
  if added_imgs is not None and len(added_imgs)>100:
    img_list.extend(added_imgs)
    score_list.extend(added_scores)
  return img_list, score_list

def finetune_scoring(img_list, score_list):  
  scale_X = StandardScaler()
  scale_y = StandardScaler()

  X = scale_X.fit_transform(img_list)
  y = scale_y.fit_transform(score_list)

  X_train,X_test,Y_train,Y_test=train_test_split(img_list, score_list, test_size=0.1, random_state=42)
  logreg = SVR(kernel='rbf', gamma='auto')
  logreg.fit(X_train,Y_train)
  return logreg
