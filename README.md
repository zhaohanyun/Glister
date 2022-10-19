# Glister
This is the project for UMJI VE441 App Development for Entrepreneurs from Group Glister. This app can help you capture good scene and moments without your operation. 

Demo: https://www.bilibili.com/video/BV1MB4y1k7wE/?vd_source=022c994e29b59e2222dc4483b4fbd068

## Getting Started
<!--
documentation on how to build and run your project. For now, simply list and provide a link to all 3rd-party tools, libraries, SDKs, APIs your project will rely on directly, that is, you don't need to list libraries that will be installed automatically as dependencies when installing the libraries you rely on directly. List both front-end and back-end dependencies. 
-->


### FrontEnd

- Install Glister/Backend/selfsigned.crt to your emulator.

<!--
Andriod camera  https://developer.android.com/guide/topics/media/camera
preview + video,audio + focus + face detection
Andriod audio  https://developer.android.com/training/wearables/user-input/voice
no audio recognition 
Image editing:  IMG.LY https://img.ly/docs/pesdk/android/introduction/getting_started/
Automatic Speech Recognition: Xunfei https://www.xfyun.cn/doc/asr/voicedictation/Android-SDK.html#_1%E3%80%81%E7%AE%80%E4%BB%8B
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

### albums and folders

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
# input: username, album name, object focus (optional), the uploaded video
# POST /postalbum?username=&albumname=&focus=&image=&video=
postAlbum(username: str, albumname: str, focus: str)


# input: username, album to edit, new album name
# GET /editalbum?username=&albumname=&newalbumname=
editAlbum(username: str, albumname: str, newAlbumname: str)

# input: username, album to delete
# GET /deletealbum?username=&albumname=
deleteAlbum(username: str, albumname: str)


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


### photos (in a folder)

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

# GET /getfavorites?username=&albumname=
getFavorites(username: str, albumname: str) -> list[Photo]

# input: photo id, whether to star or unstar the photo (1 for star, 0 for unstar)
# GET /starphoto?photoid=&star=
starPhoto(photoId: int, star: int)

# input: user name, album name, folder name, photo id to delete
# GET /deletephoto?username=&albumname=&foldername=&photoid=
deletePhoto(username: str, albumname: str, foldername: str, photoId: int)

# input: user name, album name, folder name. photo id to delete, score to replace, new folder name to replace
# POST /editphoto?username=&albumname=&foldername=&photoid=&score=&newfoldername=
editPhoto(username: str, albumname: str, foldername: str, photoId: int, score: int, newfoldername: str)
```

 ## View UI/UX
### Usability Test Results
#### Numerical Results
![usabilityTestResults](/projectManagement/images/usabilityTestResults.png)
#### Summary of Findings
- Strengths
  - Users can all upload the photos or videos they want and record the videos
  - Users can all view or star the photos easily 
  - Users all know how to download photos

- Problems
  - Hard to find the object focus feature
    - Object focus button is alongside upload button and it confuses users
  - Hard to be aware of current stage
  - The 'reco' tag on the recommended photos is confusing
  - Hard to find the save button after editing photos
    - The photo is displayed in full screen and it covers the save button

### Design Justification
![designJustification1](/projectManagement/images/designJustification1.png)
![designJustification2](/projectManagement/images/designJustification2.png)
![designJustification3](/projectManagement/images/designJustification3.png)
![designJustification4](/projectManagement/images/designJustification4.png)

### Final UI/UX Design
#### Real-Time Recording or Upload local Photos/Videos
![UIUXFeature1](/projectManagement/images/UIUXFeature1.png)
- First, users can tap on left-bottom button to define a specific object focus. Users can either input the object by text or audio.
- Second, users can upload photos or videos. There are two ways of uploading:
  - Users can tap on the record button to start real-time recording.
  - Users can tap on the middle-bottom button to upload local images or videos.

#### Get Recommended Photos
![UIUXFeature2](/projectManagement/images/UIUXFeature2.png)
- Users can view the photo gallery extracted from the video or photos they uploaded and filtered by the object focus they define.
- Users can see the score at right-bottom of the photo and the "thumb up" on left-top of the photo implies the photo is recommended.
- Users can tap on the "star" on right-top of the photo to star it as favorite.
- Users can go to the "My favorits" folder to see all the photos they have starred.

#### Save Best Moments
![UIUXFeature3](/projectManagement/images/UIUXFeature3.png)
- Users can preview the photo by tapping on the photo itself.
- Users can tap on the right-bottom button to download the photo.
- Users can tap on the right-bottom button to edit the downloaded photo.
 ## Team Roster
 <!--
 a list of team members and each member's contribution. You may simply list each member's full name for now, leaving the contribution description to the end of term. Should you want to make your GitHub public at the end of term, what do you want visitors (potential employer) to know about your contribution to this project?  
 -->
 ### Main Challenges
<!--  challenges you have encountered during the app development and how much effort you have spent on tackling them.  -->
- Machine learning(Computer Vision)-based Image Assessment System
  - Challenge 1: The black-box maching-learning model cannot be explanable to get user's trust   
    - **[Solution]**: After comprehensive literature review for image aesthetic assessment, we choose to use SAMP-Net as the deep learning model structure and [CADB](https://drive.google.com/file/d/1fpZoo5exRfoarqDvdLDpQVXVOKFW63vz/view) as the training dataset. Since the training process of SAMP-Net not only consider the color saliency map of the photo, but also assess the photo by pattern-wise importance scores, we are able to use these properties to build an explanable and more trustworth image aesthetic app. 
   - Challenge 2: It is hard for the ML model to adapt to personalized preference
     - **[Solution]**: Since the backbone deeplearning model could be continually trained, the manually scored photos from different users could be collected for further fine-tuning a more personalized photo assessment model. 
     - **[Current Limitation]**: However, since it is inapplicable to get fine-grained pattern-wise scores from users, currently we trained a regression model to predict a personalized overall aesthetic score. ***Thus, the personalized model cannot support explainability.*** Mainwhile, to avoid overfitting, only when the number of manually scored photos reaches 100, the adaptive fine-tuning process could be started.
- Front-end Design
  - Challenge 1: The APP will terminate when saving the photo to local disk by url in the list view  
    - **[Solution]**: After several days searching and studying, we decided to use a executor to do this task to avoid it from terminating, and we used two helper functions, one to generate the corresponding bitmap according to the url, the next is to save the photo to local album by the bitmap, and it can work now. 
   - Challenge 2: It is hard to complete the feature of editting photo
     - **[Solution]**: After several days search, we find it hard to use the code of other project of editting photos for our project because we require different settings of graddle, last we choose to use the Android Camera API to accomplish it. 
  - Challenge 3: Upload file fail with some local path
     - **[Solution]**: The final solution is change URI string from URL encoding to utf-8 encoding, then copy the file to cache and get a new, stable uri. This single problem takes up to 30h to solve.
  - Challenge 4: Have a hard time starting with Kotlin
     - **[Solution]**: There was little progress in the first two weeks, and we lagged in skeletal. If you cannot improve coding speed on new language with exponential rate, then it's not possible to finish task on time. At first I copy a lot, from labs and online resources, and try to make slight changes to make codes serve for me. Later it becomes easier and I can create my own pieces.
  - Challenge 5: Emulator not stable
     - **[Solution]**: All of us have a hard time with running emulator on our hardware. Slow, and always crashes. The most serious case, one of our frontend colleague cannot run emulator at all. And some bug is purely due to emulator, but is quite confusing for debugging. No good solution, just try and rerun again and again.
- Back-end Construction
  - Challenge 1: The video extraction function always reverse the portrait videos and cause errors to object detection
    - **[Solution]**: We searched online for functions detecting the intrinsic rotation flag in the video and rotate the video according to it before extraction
  - Challenge 2: Debugging for backend is tedious and it's hard for us to see the output error, especially for SQL statements
    - **[Solution]**: We first use "service gunicorn status" to see the printing messages. We then debug SQL statements by directing entering the database and type in the statements into terminal. We also write scripts for resetting database and directories so that we can start over if something bad happens.

 ### Tean Members and Personal Contribution
- Enzhi Zhang ez_zhang@sjtu.edu.cn

  - Finish the front end work of the feature of "View scored photos".
  - Finish the front end work of the feature of "Get recommended photos".
  - Finish the front end work of the feature of "Star photos".
  - Finish the feature of "Download photos to local".
  - Finish the feature of "Adjust photos".
  - Finish the UI of "manually score photos".
  - Finish the UI of "delete unwanted photos".
  
   
  


- Hanyun Zhao zhaohanyun@sjtu.edu.cn 

  - Finish the feature of recording

  - Finish the feature of upload photos and videos

  - Finish the feature of specify object by text and by speech

  - Finish the feature of star photos

  - Finish the feature of edit photo galleries

  - Finish the folder structure (group the photos)

  - Finish the feature of manually group photos

  - Finish the feature of manually score the photos

  - Finish the feature of remove unwanted photos

  - Fix feature of download, fix controller of MVC

  - Implement all APIs in front end

  - Debug everything in frontend, co-testing with backend colleagues

    

-  Simin Fan olivia-fsm@sjtu.edu.cn
   - Engine architecture and systematic design
   - Preprocess the CADB image aesthetic scoring training dataset [[processed CADB](https://drive.google.com/file/d/1scdHgmPOmiNzhdpqhCDdNW2K184b_7rp/view?usp=sharing)]
   - Finish testing and deploying image object relatedness scoring model
   - Finish finetuning and deploying image aesthetic-base assessment scoring model
   - Finish backend function for image aesthetic score explanation with Saliency Map and pattern-wise importance scoring
   - Implement front-end design for folder rename, manually image scoring with rating bar and image aesthetic score explanation
   - Implement fine-tuning SAMPNET with manually scored images (only when amount>100)

- Yuchen Xu tonyxu0305@sjtu.edu.cn
  -  Finish the UI design and UI flow
  -  Finish the front-face interface design
  -  Implement API in back-end
  -  Finish making the video of UI demo and presentation
  
-  Yuqing Qiu qiuyuqing@sjtu.edu.cn 
   -  Finish writing and debugging backend API "getAlbums", "getFolders" and "getPhotos" for the feature of "view scored photos" and "get recommended photos"
   -  Finish writing and debugging backend API "editAlbum" for the feature of "rename the photo folder
   -  Finish writing and debugging backend API "starPhoto" for the feature of "star photos"
   -  Finish writing backend API "deletePhoto" for the feature of "modify photos"
   -  Finish writing backend API "postAlbum" and subfunctions "processVideo" and "processImages" for the feature of "Real-time recording", "Upload photos" and "Upload videos"
   -  Finish integrating the algorithm with subfunctions "processVideo" and "processImages"
   -  Finish setting up the database
   -  Finish revising the “Extract Video” function to deal with both portrait and landscape photos or videos.
   -  Finish adding “object focus” input for the backend API “postAlbum” so that users are able to specify the object focus they want to capture.
   
-  Zechen Huang huangzechen@sjtu.edu.cn  
   - Deploy the backend frameworks and tools on the cloud server.
   - Configure the backend directory and write shell scripts to control the app.
   - Test and debug skeletal and MVP functions.
   - Write the APIs of the backend functions.
