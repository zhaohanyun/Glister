## *user identification

> 可以先不用这一块；
>
> 后面的函数的参数里(username, albumname)什么的用来控制用户session和权限的，也可以先不用管；

```python
login(username: str, password: str)
```



## albums and folders

```python
# input: username
# output: a list of albums of this user (e.g. Jay Chou concert, NBA live)
# GET /getalbums?username=
getAlbums(username: str) -> list[str]


# Upload video to the server, and create a new album.
# input: username, album name, the uploaded video
# output: a list of albums of this user
# POST /postalbum?username=&albumname=&image=&video=
postAlbum(username: str, albumname: str, video) -> list[str]


# input: username, album to edit, new album name
# output: a list of albums of this user
# GET /editalbum?username=&albumname=&newalbumname=
editAlbum(username: str, albumname: str, newAlbumname: str) -> list[str]


# input: username, album to open
# output: a list of foldernames in the album
# GET /getfolders?username=&albumname=
getFolders(username: str, albumname: str) -> list[str]

```



## photos (in a folder)

```python
Photo:
    photoId int
    photoUri str
    isRecommended bool
    isStarred bool
    score int

# input: user name, album name, folder to open (e.g. default/tree/people/favourite)
# output: a list of photos in this folder
# GET /getfolders?username=&albumname=&foldername=&
getPhotos(username: str, albumname: str, foldername: str) -> list[Photo]

# input: ..., whether to star or unstar the photo (1 for star, 0 for unstar)
# output: editted photo
# GET /starphoto?photoid=&star=
starPhoto(photoId: int, star: int) -> Photo

# input: ..., photo to delete
# GET /deletephoto?username=&albumname=&foldername=&photoid=
deletePhoto(username: str, albumname: str, foldername: str, photoId: int)
```

