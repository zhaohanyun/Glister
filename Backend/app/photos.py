import json
import os, time
from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from django.core.files.storage import FileSystemStorage


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
        SELECT p.photoId, p.photoName AS photo_url, p.photoscore AS score
        p.isRecommended, p.isStarred
        FROM photos p 
        LEFT OUTER JOIN folders f ON p.folderId = f.folderId
        LEFT OUTER JOIN albums a ON a.albumId = p.albumId
        WHERE p.owner = ? AND a.albumname = ? AND f.foldername = ?
        ORDER BY p.photoscore DESC;
        ''',
        (username, albumname, foldername, )
    )
    rows = cursor.fetchall()
    for row in rows:
        row['photoname'] = "{}{}/{}/{}/{}".format(settings.MEDIA_URL, username, albumname, foldername, row['photoname'])
    response = {}
    response['photos'] = rows
    return JsonResponse(response)


def deletePhoto(request):
    if request.method != 'DELETE':
        return HttpResponse(status=404)
    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    foldername = request.GET.get("foldername")
    photoId = request.DELETE.get("photoid")

    # delete from database
    cursor = connection.cursor()
    cursor.execute(
        '''
        DELETE FROM photos
        WHERE photoid = ?;
        ''',
        (photoId, )
    )

    # get photoname
    cursor.execute(
        '''
        SELECT p.photoname
        FROM photos p
        WHERE p.photoId = ?;
        ''',
        (photoId, )
    )
    photoname = cursor.fetchall()[0]
    
    # delete from fileStorage
    dirPath = settings.MEDIA_ROOT / username / albumname / foldername;
    fs = FileSystemStorage(location=dirPath)
    fs.delete(photoname)
    return JsonResponse({})


def starPhoto(request):
    if request.method != 'PUT':
            return HttpResponse(status=404)
    photoId = request.PUT.get("photoid")
    star = request.PUT.get("star")
    cursor = connection.cursor()
    cursor.execute(
        '''
        UPDATE photos
        set isStarred = ?
        WHERE photoid = ?;
        ''',
        (star, photoId, )
    )
    return JsonResponse({})
