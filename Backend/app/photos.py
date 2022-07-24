import json
import os, time
from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from django.views.decorators.csrf import csrf_exempt

"""
{
    "photos": [{"photoId": 0, "photo_url": photo_url1, "score": score1, "isRecommended": 0, "isStarred": 0},
               {"photoId": 1, "photo_url": photo_url2, "score": score2, "isRecommended": 1, "isStarred": 1},
               ...
              ]
}
"""

def getPhotos(request):
    if request.method != 'GET':
        return HttpResponse(status=404)

    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    foldername = request.GET.get("foldername")
    cursor = connection.cursor()
    cursor.execute(
        '''
        SELECT p.photoId, p.photoname, p.photoScore,
        p.isRecommended, p.isStarred
        FROM photos p
        LEFT OUTER JOIN folders f ON p.folderId = f.folderId
        LEFT OUTER JOIN albums a ON a.albumId = p.albumId
        WHERE p.owner = '{}' AND a.albumname = '{}' AND f.foldername = '{}'
        ORDER BY p.photoScore DESC;
        '''
        .format(username, albumname, foldername)
    )
    rows = cursor.fetchall()
    photos = []
    for row in rows:
        photo = {}
        photo['photoId'] = row[0]
        photo['photo_url'] = "{}{}/{}/{}/{}.".format(settings.MEDIA_URL, username, albumname, foldername, row[1])
        photo['score'] = row[2]
        photo['isRecommended'] = row[3]
        photo['isStarred'] = row[4]
        photos.append(photo)
    response = {}
    response['photos'] = photos
    return JsonResponse(response)


@csrf_exempt
def deletePhoto(request):
    if request.method != 'GET':
        return HttpResponse(status=404)
    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    foldername = request.GET.get("foldername")
    photoId = request.GET.get("photoid")

    # get photoname
    cursor = connection.cursor()
    cursor.execute(
        '''
        SELECT p.photoname
        FROM photos p
        WHERE p.photoId = {};
        '''
        .format(photoId)
    )
    photoname = cursor.fetchall()[0][0]
    
    # delete from fileStorage
    dirPath = settings.MEDIA_ROOT / username / albumname / foldername;
    fs = FileSystemStorage(location=dirPath)
    fs.delete(photoname)
    
    # delete from database
    cursor.execute(
        '''
        DELETE FROM photos
        WHERE photoid = {};
        '''
        .format(photoId)
    )
    return JsonResponse({})


def starPhoto(request):
    if request.method != 'GET':
            return HttpResponse(status=404)
    photoId = request.GET.get("photoid")
    star = request.GET.get("star")
    cursor = connection.cursor()
    cursor.execute(
        '''
        UPDATE photos
        set isStarred = {}
        WHERE photoid = {};
        '''
        .format(star, photoId)
    )
    return JsonResponse({})


def getFavorites(request):
    if request.method != 'GET':
        return HttpResponse(status=404)
    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    cursor = connection.cursor()
    cursor.execute(
        '''
        SELECT p.photoId, p.photoname, p.photoScore,
        p.isRecommended, p.isStarred
        FROM photos p
        LEFT OUTER JOIN albums a ON a.albumId = p.albumId
        WHERE p.owner = '{}' AND a.albumname = '{}' AND p.isStarred = 1
    
        '''.format(username, albumname)
    )
    rows = cursor.fetchall()
    photos = []
    for row in rows:
        photo = {}
        photo['photoId'] = row[0]
        photo['photo_url'] = "{}{}/{}/{}/{}.".format(settings.MEDIA_URL, username, albumname, foldername, row[1])
        photo['score'] = row[2]
        photo['isRecommended'] = row[3]
        photo['isStarred'] = row[4]
        photos.append(photo)
    response = {}
    response['photos'] = photos
    return JsonResponse(response)


