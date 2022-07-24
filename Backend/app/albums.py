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
#from videoExtract.extract import *
from app.SAMPNet.image_scoring import score_images

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
        WHERE a.owner = '{}'
        ORDER BY a.created DESC;
        '''
        .format(username)
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
        WHERE a.owner = '{}' AND a.albumname = '{}'
        ORDER BY f.folderId;
        '''
        .format(username, albumname, )
    )
    rows = cursor.fetchall()
    response = {}
    response['folders'] = rows
    return JsonResponse(response)


@csrf_exempt
def editAlbum(request):
    if request.method != 'GET':
        return HttpResponse(status=404)
    
    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    newalbumname = request.GET.get("newalbumname")

    cursor = connection.cursor()
    cursor.execute(
        '''
        UPDATE albums
        set albumname = '{}'
        WHERE owner = '{}' AND albumname = '{}';
        '''
        .format(newalbumname, username, albumname, )
    )
    dir = settings.MEDIA_ROOT / username / albumname;
    newDir = settings.MEDIA_ROOT / username / newalbumname;
    os.rename(str(dir), str(newDir))
    return JsonResponse({})

@csrf_exempt
def postAlbum(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    
    # loading form-encoded data
    username = request.POST.get("username")
    albumname = request.POST.get("albumname")
    focus = request.POST.get("focus")
    # print("postalbum()", username, albumname)
    # create new album and add to database
    cursor = connection.cursor()
    cursor.execute(
        '''
        INSERT INTO albums (albumname, owner)
        VALUES
                ('{}', '{}');
        '''
        .format(albumname, username, )
    )
    albumId = cursor.lastrowid 
    # process images or videos
    dirpath = settings.MEDIA_ROOT / username / albumname;
    os.makedirs(dirpath)
    if request.FILES.get("image"):
        content = request.FILES['image']
        filename = "{}_{}_{}.jpg".format(username, albumname, str(round(time.time())))
        fs = FileSystemStorage(location=dirpath / "tmp")
        filename = fs.save(filename, content)
        processImages(username, albumname, albumId, focus, "tmp", cursor)
    elif request.FILES.get("video"):
        content = request.FILES['video']
        filename = "{}_{}_{}.mp4".format(username, albumname, str(round(time.time())))
        fs = FileSystemStorage(location=dirpath)
        filename = fs.save(filename, content)
        processVideo(username, albumname, albumId, focus, filename, cursor)
    else:
        print("postAlbum() unknown file format:", request.FILES)
    return JsonResponse({})


def processVideo(username, albumname, albumId, focus, filename, cursor):
    albumPath = settings.MEDIA_ROOT / username / albumname
    filePath = albumPath / filename
    # extract photos from videos
    os.system('python3 /root/Glister/Backend/app/videoExtract/extract.py --video {} --sampling 0.5 --output-root {} 2> out.txt'.format(str(filePath), str(albumPath)))
    outputDir = filename[:-4] + '_frames'
    os.remove(filePath)
    processImages(username, albumname, albumId, focus, outputDir, cursor)
    # print("process video done!")


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
def processImages(username, albumname, albumId, focus, outputDir, cursor):
    albumPath = settings.MEDIA_ROOT / username / albumname
    outputPath = albumPath / outputDir
    scoreResults = score_images(str(outputPath))
    results = {}

    if focus != None:
        results[focus] = scoreResults[focus] if focus in scoreResults else []
    else:
        results = scoreResults

    for foldername in results:
        # create new folder and add to database
        folderPath = albumPath / foldername
        os.mkdir(folderPath)
        # print("processImage()", foldername, albumId, username)
        cursor.execute(
            '''
            INSERT INTO folders (foldername, albumId, owner)
            VALUES
                    ('{}', {}, '{}');
            '''
            .format(foldername, albumId, username)
        )
        folderId = cursor.lastrowid

        for idx, rst in enumerate(results[foldername]):
            # move file and add to database
            filename = rst['filename']
            score = rst['score']
            isRecommended = rst['isRecommended']
            newFileName = "{}_{}_{}_{}.jpg".format(username, albumname, foldername, idx)
            oldFilePath = outputPath / filename
            newFilePath = folderPath / newFileName
            shutil.copy(str(oldFilePath), str(newFilePath))
            # print("inserting into photos ", newFileName, folderId, albumId, username)
            cursor.execute(
                '''
                INSERT INTO photos (photoname, photoScore, isRecommended, folderId, albumId, owner)
                VALUES
                        ('{}', {}, {}, {}, {}, '{}');
                '''
                .format(newFileName, score, isRecommended, folderId, albumId, username)
            )
    shutil.rmtree(outputPath)
    # print("Process image done!")

@csrf_exempt
def deleteAlbum(request):
    if request.method != 'POST':
             return HttpResponse(status=404)
    username = request.GET.get("username")
    albumname = request.GET.get("albumname")
    print(username, 'is deleting', albumname)
    
    dirPath = settings.MEDIA_ROOT / username / albumname
    shutil.rmtree(dirPath)
    cursor = connection.cursor()
    cursor.execute('PRAGMA foreign_keys=ON')
    cursor.execute(
        '''
        DELETE FROM albums
        WHERE albumname = '{}';
        '''
        .format(albumname)
    )


    return JsonResponse({})
