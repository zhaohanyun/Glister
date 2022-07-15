# Glister
This is the project for UMJI VE441 App Development for Entrepreneurs from Group Glister. This app can help you capture good scene and moments without your operation. 



## Getting Started
<!--
documentation on how to build and run your project. For now, simply list and provide a link to all 3rd-party tools, libraries, SDKs, APIs your project will rely on directly, that is, you don't need to list libraries that will be installed automatically as dependencies when installing the libraries you rely on directly. List both front-end and back-end dependencies. 
-->

### FrontEnd

- Image editing:  IMG.LY https://img.ly/docs/pesdk/android/introduction/getting_started/
- Automatic Speech Recognition: Xunfei https://www.xfyun.cn/doc/asr/voicedictation/Android-SDK.html#_1%E3%80%81%E7%AE%80%E4%BB%8B
  

<!--
Andriod camera  https://developer.android.com/guide/topics/media/camera
preview + video,audio + focus + face detection
Andriod audio  https://developer.android.com/training/wearables/user-input/voice
no audio recognition
-->


### Algorithm
- [Aesthetic-based Image Assessment Module (SAMP-Net)](https://github.com/bcmi/Image-Composition-Assessment-Dataset-CADB)
- [Object Detection API](https://github.com/OlafenwaMoses/ImageAI)

### Backend
- Rest API: https://restfulapi.net/
- Database: https://www.w3schools.com/php/php_mysql_intro.asp


## Model and Engine
<!--
put a copy of your Storymap here.  List all components of your engine architectures and how they tie together. Draw a block diagram showing your data and control flows in the engine. For each block, describe how the functionalities will be implemented. If your app doesn't have its own engine, describe how you will use the OS sub-systems or 3rd-party SDKs to build your app. You can re-use your engine architecture slides from the DRAFT portion of this assignment, but they should be accompanied by descriptive explanation, e.g., the talk to give accompanying each slide. 
-->
![engineArchitecture](/projectManagement/images/engineArchitecture.png)

### Frontend
- **Camera API**: It depends on Andriod built-in camera APIs. We will use functionalities such as `takephotos`, `takevideos`, `zoom`, `focus` and `crop`.
- **Speech Recognition API**: It depends on Andriod built-in `SpeechRecognizer` class. It streams audio to remote servers to perform speech recognition.
### Algorithm:
- **Video-clip Handler**: Its function is transfroming a video to a image set. We will simply take video frames that are evenly distributed over the time.
- **Image Filter**: Its function is filtering images by specific object focus. We will employ an object detection deep learning algorithm to judge whether the image contains the object and filter it out if not.
- **Image Scorer**: Its function is giving aesthetic scores to images by quality. We will employ an image scoring deep learning algorithm to score each image based on its clarity, lightness, colorfulness and etc.
### Backend:
- **Cloud Server**: We will deploy our backend on an AWS cloud server. We will store all the images and do back-end processing on this server. We will use a MySQL database to store all the paths of images, their scores and classes.
- **Local File DB**: The local photo album of the user's phone.



## APIs and Controller
<!--
 describe how your front-end would communicate with your engine: list and describe your APIs. This is only your initial design, you can change them again later, but you should start thinking about and designing how your front end will communicate with your engine. If you're using existing OS subsystem(s) or 3rd-party SDK(s) to implement your engine, describe how you will interact with these.
 -->

<!-- - `POST /image`
  
  Use **OkHttp SDK** to upload a photo to server using `multipart/form-data` representation/encoding.
  
  *request parameters*:
  
  | form-data part | value        |
  | -------------- | ------------ |
  | `username`     | user name    |
  | `project_name` | project name |
  | `image`        | image file   |
  
  *Response code*:  
  
  - 200 if success  
  - 404 if user or project does not exist  
  
- `POST /video`

  Use **OkHttp SDK** to upload a video to server using `multipart/form-data` representation/encoding.

  *request parameters*:

  | form-data part | value        |
  | -------------- | ------------ |
  | `username`     | user name    |
  | `project_name` | project name |
  | `video`        | video file   |
  
  *Response code*:  
  
  - 200 if success  
  - 404 if user or project does not exist  
  
- `POST /project?username=&project_name=`

  Create a new project for a certain user.

  *Response code*:  

  - 200 if success
  - 404 if user not exist
  - 409 if project name already exist  

- `DELETE /project?username=&project_name=`

  Delete a project of a certain user, or do nothing if project name not exist.

  *Response code*:  

  - 200 if success
  - 404 if user does not exist  

- `GET /image?username=&project_name=`

  *Return*: a JSON object that contains a list of all urls and a list of all scores of images in this project. And later use these url to download previews for each image in the project. We use the image view `.load()` extension from the **Coil SDK** to download the image directly from its URL under expected resolution. <!--also employing a crossfade effect to *mimic* progressive JPEG.-->

  <!-- ```javascript
  {
      "urls":[url1, url2, ...],
      "scores":[score1, score2, ...]
  }
  ```

  *Response code*:  

  - 200 if success
  - 404 if user or project does not exist  

- `POST /focus?username=&project_name=&focus=[enum]`

  Set the what kind of object you would like to focus on, which will be used in image filtering, scoring and processing. The value of `focus` must be one of the items that allowed by our system. A example for our possible`focus` set could be `Enum("car", "flower", "face", ...)`.

  *Response code*:  

  - 200 if success
  - 404 if user or project does not exist
  - 422 if invalid focus  
    
   -->

## albums and folders

```python
# input: username
# output: a list of albums of this user
"""
{
    "albums": ["albumname1", "albumname2", "albumname3", ...]
}
"""
# GET /getalbums?username=
getAlbums(username: str) -> list[str]

# Upload video to the server, and create a new album.
# input: username, album name, the uploaded video
# POST /postalbum?username=&albumname=&image=&video=
postAlbum(username: str, albumname: str, video)


# input: username, album to edit, new album name
# GET /editalbum?username=&albumname=&newalbumname=
editAlbum(username: str, albumname: str, newAlbumname: str)


# input: username, album to open
# output: a list of foldernames in the album
"""
{
    "folders": ["foldername1", "foldername2", "foldername3", ...]
}
"""
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
"""
{
    "photos": [{"photoId": 0, "photo_url": photo_url1, "score": score1, "isRecommended": 0, "isStarred": 0},
               {"photoId": 1, "photo_url": photo_url2, "score": score2, "isRecommended": 1, "isStarred": 1},
               ...
              ]
}
"""
# GET /getfolders?username=&albumname=&foldername=&
getPhotos(username: str, albumname: str, foldername: str) -> list[Photo]


# input: photo id, whether to star or unstar the photo (1 for star, 0 for unstar)
# GET /starphoto?photoid=&star=
starPhoto(photoId: int, star: int)

# input: user name, album name, folder name, photo id to delete
# GET /deletephoto?username=&albumname=&foldername=&photoid=
deletePhoto(username: str, albumname: str, foldername: str, photoId: int)
```



 ## View UI/UX
 <!--
 leave this section blank for now.  You will populate it with your UI/UX design in a latter assignment. 
 -->

 ## Team Roster
 <!--
 a list of team members and each member's contribution. You may simply list each member's full name for now, leaving the contribution description to the end of term. Should you want to make your GitHub public at the end of term, what do you want visitors (potential employer) to know about your contribution to this project?  
 -->

- Enzhi Zhang ez_zhang@sjtu.edu.cn

  - ​     Finish the UI of the feature of "View scored photos".


  - ​     Finish the front end work of the feature of "Get recommended photos".


  - ​     Finish the front end work of the feature of "Star photos".


  - ​     Finish the feature of "Download photos to local".


  - ​     Finish the feature of "Adjust photos".



- Hanyun Zhao zhaohanyun@sjtu.edu.cn 

  - Finish the feature of recording

  - Finish the feature of upload photos and videos
  - Finish the feature of specify object in front end

  - Implement API in front end

    

-  Simin Fan olivia-fsm@sjtu.edu.cn
-  Yuchen Xu tonyxu0305@sjtu.edu.cn
-  Yuqing Qiu qiuyuqing@sjtu.edu.cn 
-  Zechen Huang huangzechen@sjtu.edu.cn  
