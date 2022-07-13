import json
import os, time
from django.shortcuts import render
from django.http import JsonResponse, HttpResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from django.core.files.storage import FileSystemStorage
from pathlib import Path

from Backend.routing.settings import MEDIA_ROOT, MEDIA_URL

"""
{
    "albums": ["albumname1", "albumname2", "albumname3", ...]
}
"""

def getAlbums(request):
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

def getFolders(request):
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


def postAlbum(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    
    # loading form-encoded data
    username = request.POST.get("username")
    albumname = request.POST.get("albumname")

    # create new album for the user
    cursor = connection.cursor()
    cursor.execute(
        '''
        INSERT INTO albums (albumname, owner)
        FROM folders f
        LEFT OUTER JOIN albums a ON f.albumId = a.albumId
        WHERE a.owner = ? AND a.albumname = ?
        ORDER BY f.folderId;
        ''',
        (username, albumname, )
    )

    # process images or videos
    dirPath = MEDIA_ROOT / username / albumname;
    baseUrl = '{}{}/{}/'.format(MEDIA_URL, username, albumname)
    if request.FILES.get("image"):
        content = request.FILES['image']
        filename = username+albumname+str(time.time())+".jpeg"
        fs = FileSystemStorage(location=dirPath, base_url=baseUrl)
        filename = fs.save(filename, content)
        processImage(username, albumname, fs)
    elif request.FILES.get("video"):
        content = request.FILES['video']
        filename = username+albumname+str(time.time())+".mp4"
        fs = FileSystemStorage(location=dirPath, base_url=baseUrl)
        filename = fs.save(filename, content)
        processVideo(username, albumname, fs)

    return JsonResponse({})

def processImage(username, albumname, fs):
    pass

def processVideo(username, albumname, fs):
    pass

