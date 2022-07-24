## *user identification

> 可以先不用这一块；
>
> 后面的函数的参数里(username, albumname)什么的用来控制用户session和权限的，也可以先不用管；

```python
login(username: str, password: str)
```



## albums and folders

```python
# GET /getalbums?username=
# input: username
# output: a list of album names of this user (e.g. Jay Chou concert, NBA live)
#          {"albums": ["albumname1", "albumname2", "albumname3", ...]}
getAlbums(username: str) -> list[str]


# POST /postalbum?username=&albumname=&image=&video=
# Upload video to the server, and create a new album.
# input: username, album name, the uploaded video
# output: {}
postAlbum(username: str, albumname: str, video)


# GET /editalbum?username=&albumname=&newalbumname=
# input: username, album to edit, new album name
# output: {}
editAlbum(username: str, albumname: str, newAlbumname: str)


# GET /getfolders?username=&albumname=
# input: username, album to open
# output: a list of foldernames in the album
#	      {"folders": ["foldername1", "foldername2", "foldername3", ...]}
getFolders(username: str, albumname: str) -> list[str]



# POST /deleteAlbum?username=&albumname=
# input: user name and album name to be deleted
# output: {}
deleteAlbum(username: str, albumname: str)
```



## photos (in a folder)

```python
Photo:
    photoId int
    photoUri str
    isRecommended bool
    isStarred bool
    score int

    
# GET /getfolders?username=&albumname=&foldername=&
# input: user name, album name, folder to open (e.g. default/tree/people/favourite)
# output: a list of photos in this folder
getPhotos(username: str, albumname: str, foldername: str) -> list[Photo]


# GET /starphoto?photoid=&star=
# input: ..., whether to star or unstar the photo (1 for star, 0 for unstar)
# output: {}
starPhoto(photoId: int, star: int) 

# GET /deletephoto?username=&albumname=&foldername=&photoid=
# input: ..., photo to delete
deletePhoto(username: str, albumname: str, foldername: str, photoId: int)

# GET /getfavorites?username=&albumname=&
# input: username, album name
# output: a list of starred photos in the album
getFavorites(username: str, albumname: str) -> list[Photo]
```

