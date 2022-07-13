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


def editAlbum(request):
    if request.method != 'PUT':
        return HttpResponse(status=404)
    
    username = request.PUT.get("username")
    albumname = request.PUT.get("albumname")
    newalbumname = request.PUT.get("newalbumname")

    cursor = connection.cursor()
    cursor.execute(
        '''
        UPDATE albums
        set albumname = ?
        WHERE owner = ? AND albumname = ?;
        ''',
        (newalbumname, username, albumname, )
    )
    dir = settings.MEDIA_ROOT / username / albumname;
    newDir = settings.MEDIA_ROOT / username / newalbumname;
    os.rename(str(dir), str(newDir))
    return JsonResponse({})


def postAlbum(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    
    # loading form-encoded data
    username = request.POST.get("username")
    albumname = request.POST.get("albumname")

    # create new album and add to database
    cursor = connection.cursor()
    cursor.execute(
        '''
        INSERT INTO albums (albumname, owner)
        VALUES
                (?, ?);
        ''',
        (albumname, username, )
    )

    # process images or videos
    dirpath = settings.MEDIA_ROOT / username / albumname;
    baseurl = '{}{}/{}/'.format(settings.MEDIA_URL, username, albumname)
    os.makedirs(dirpath)
    if request.FILES.get("image"):
        content = request.FILES['image']
        filename = username+albumname+str(time.time())+".jpeg"
        fs = FileSystemStorage(location=dirpath, base_url=baseurl)
        filename = fs.save(filename, content)
        processImages(username, albumname, filename, cursor)
    elif request.FILES.get("video"):
        content = request.FILES['video']
        filename = username+albumname+str(time.time())+".mp4"
        fs = FileSystemStorage(location=dirpath, base_url=baseurl)
        filename = fs.save(filename, content)
        processVideo(username, albumname, filename, cursor)
    return JsonResponse({})


def processVideo(username, albumname, filename, cursor):
    albumPath = settings.MEDIA_ROOT / username / albumname
    filePath = albumPath / filename
    # extract photos from videos
    os.system('python3 utils/extract.py --video {} --sampling 0.5 --output-root {}'.format(str(filePath), str(albumPath)))
    # not sure whether to use fileStorageSystem here
    os.remove(filePath)
    processImages(username, albumname, filename, cursor)


def processImages(username, albumname, filename, cursor):
    albumPath = settings.MEDIA_ROOT / username / albumname
    filePath = albumPath / filename
    # classify and score photos
    results = classifyAndScorePhotos(str(filePath))
    for foldername in results:
        # create new folder and add to database
        folderPath = albumPath / foldername
        os.mkdir(folderPath)
        albumId = cursor.lastrowid
        cursor.execute(
            '''
            INSERT INTO folders (foldername, albumId, owner)
            VALUES
                    (?, ?, ?);
            ''',
            (foldername, albumId, username, )
        )

        for idx, (filename, score, isRecommended) in enumerate(results[foldername].items()):
            # move file and add to database
            newFileName = "{}_{}_{}_{}".format(username, albumname, foldername, idx)
            oldFilePath = albumPath / filename
            newFilePath = folderPath / newFileName
            shutil.move(str(oldFilePath), str(newFilePath))
            folderId = cursor.lastrowid
            cursor.execute(
                '''
                INSERT INTO photos (photoname, photoScore, isRecommended, folderId, albumId, owner)
                VALUES
                        (?, ?, ?, ?, ?, ?);
                ''',
                (newFileName, score, isRecommended, folderId, albumId, username)
            )


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
def classifyAndScorePhotos(filePath):
    filenames = os.listdir(filePath)
    results = {"cats" : [], "dogs" : []}
    folders = ['cats', 'dogs']
    for i, filename in enumerate(filenames):
        score = random.randint(10, 100)
        foldername = folders[0 if i < len(filenames) / 2 else 1]
        results[foldername].append({'filename': filename, 
                                    'score': score,
                                    'isRecommended': 1 if score > 60 else 0})

