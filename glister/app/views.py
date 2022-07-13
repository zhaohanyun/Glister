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
    "albums": ["albumname1", "albumname2", "albumname3", ...]
}
"""

def getalbums(request):
    if request.method != 'GET':
        return HttpResponse(status=404)

    username = request.GET.get("username")
    cursor = connection.cursor()
    cursor.execute(
        '''
        SELECT a.albumname
        FROM albums a
        WHERE a.owner = ? 
        ORDER BY a.created DESC;
        ''',
        (username, )
    )
    rows = cursor.fetchall()
    response = {}
    response['albums'] = rows
    return JsonResponse(response)

"""
{
    "folders": ["foldername1", "foldername2", "foldername3", ...]
}
"""

def getfolders(request):
    if request.method != 'GET':
        return HttpResponse(status=404)

    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    cursor = connection.cursor()
    cursor.execute(
        '''
        SELECT f.foldername
        FROM folders f
        LEFT OUTER JOIN albums a ON f.albumId = a.albumId
        WHERE a.owner = ? AND a.albumname = ?
        ORDER BY f.folderId;
        ''',
        (username, albumname, )
    )
    rows = cursor.fetchall()
    response = {}
    response['folders'] = rows
    return JsonResponse(response)

"""
{
    "photos": [{"photo_url": photo_url1, "score": score1, "isRecommended": 0, "isStarred": 0},
               {"photo_url": photo_url2, "score": score2, "isRecommended": 1, "isStarred": 1},
               ...
              ]
}
"""

def getphotos(request):
    if request.method != 'GET':
        return HttpResponse(status=404)

    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    foldername = request.GET.get("foldername")
    cursor = connection.cursor()
    cursor.execute(
        '''
        SELECT p.photoName AS photo_url, p.photoscore AS score
        p.isRecommended, p.isStarred
        FROM photos p 
        LEFT OUTER JOIN folders f ON p.folderId = f.folderId
        LEFT OUTER JOIN albums a ON a.albumId = p.albumId
        WHERE p.owner = ? AND a.albumname = ? AND f.foldername = ?
        ORDER BY p.photoscore DESC;''',
        (username, albumname, foldername, )
    )
    rows = cursor.fetchall()
    for row in rows:
        row['photo_url'] = "/uploads/{}/{}/{}/{}".format(username, albumname, foldername, row['photo_url'])
    response = {}
    response['photos'] = rows
    return JsonResponse(response)