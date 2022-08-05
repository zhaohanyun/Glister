package cn.edu.sjtu.glister.glisterfrontend

/*import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue*/

import android.util.Log
import androidx.databinding.ObservableArrayList
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties


//can we merge this file to HttpHandler?
object ImageStore {

    private val _images = arrayListOf<Image>()
    //val chatts: List<Chatt> = _chatts
    private val nFields = Image::class.declaredMemberProperties.size

    //private lateinit var queue: RequestQueue
    //private const val serverUrl ="https://52.39.198.75/"
    //private val serverUrl : String=  Resources.getSystem().getString(R.string.Hanyun_server)
    private const val serverUrl ="https://106.14.1.108/"
    private val client = OkHttpClient()


    val images = ObservableArrayList<Image>()

    fun getImages(username:String,albumname:String, foldername:String) {
//        # input: user name, album name, folder to open (e.g. default/tree/people/favourite)
//        # output: a list of photos in this folder
//        # GET /getphotos?username=&albumname=&foldername=&

//        var request :Request
//        if(albumname!="myfavorites") {
//            val getPhotosUrl = (serverUrl + "getphotos/").toHttpUrl().newBuilder()
//                .addQueryParameter("username", username)
//                .addQueryParameter("albumname", albumname)
//                .addQueryParameter("foldername", foldername)
//                .build()
//            request = Request.Builder()
//                .url(getPhotosUrl)
//                .build()
//        }else{
//            val getFavoritesUrl = (serverUrl+"getfavorites/").toHttpUrl().newBuilder()
//                .addQueryParameter("username", username)
//                .addQueryParameter("albumname", albumname)
//                .build()
//            request = Request.Builder()
//                .url(getFavoritesUrl)
//                .build()
//        }
        if (foldername=="myfavorites") {
            getFavorites(username,albumname)
        }
        else {
            val getPhotosUrl = (serverUrl + "getphotos/").toHttpUrl().newBuilder()
                .addQueryParameter("username", username)
                .addQueryParameter("albumname", albumname)
                .addQueryParameter("foldername", foldername)
                .build()
            val request = Request.Builder()
                .url(getPhotosUrl)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("getPhotos", "Failed GET request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val imagesReceived = try {
                            JSONObject(response.body?.string() ?: "").getJSONArray("photos")
                        } catch (e: JSONException) {
                            JSONArray()
                        }

                        images.clear()
                        for (i in 0 until imagesReceived.length()) {
                            //val imageEntry = JSONArray(imagesReceived[i])//as JSONObject
                            val imageEntry =
                                Gson().fromJson(imagesReceived[i].toString(), Image::class.java)
                                    ?: null

                            if (imageEntry !== null) {
                                images.add(imageEntry)
//                          Photo:{
//                          photoId int
//                          photoUri str
//                          isRecommended bool
//                          isStarred bool
//                          score int}
//                            images.add(Image(
//                                imageId = imageEntry[0] as Int?,
//                                imageUrl = imageEntry[1].toString(),
//                                isRecommended = imageEntry[2] as Boolean,
//                                isStarred = imageEntry[3] as Boolean,
//                                score = imageEntry[4] as Int?
//                            ))
                            } else {
                                Log.e("getPhotos", "not a Photo object")
                            }
                        }
                    }
                }
            })
        }
    }

    fun getFavorites(username: String, albumname: String) {
//    # GET /getfavorites?username=&albumname=
//    getFavorites(username: str, albumname: str) -> list[Photo]
        println("get favorites")
        println(username)
        println(albumname)
        val getFavoritesUrl = (serverUrl+"getfavorites/").toHttpUrl().newBuilder()
            .addQueryParameter("username", username)
            .addQueryParameter("albumname", albumname)
            .build()
        val request = Request.Builder()
            .url(getFavoritesUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                Log.e("getFavorites", "Failed GET getFavorites request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val imagesReceived = try { JSONObject(response.body?.string() ?: "").getJSONArray("photos") } catch (e: JSONException) { JSONArray() }
                    images.clear()
                    for (i in 0 until imagesReceived.length()) {
                        //val imageEntry = JSONArray(imagesReceived[i])//as JSONObject
                        val imageEntry = Gson().fromJson(imagesReceived[i].toString(),Image::class.java)?:null
                        if (imageEntry !== null) {
                            images.add(imageEntry)
                        }
//                         else {
//                            Log.e("getfavorites", "fail")
//                        }
                    }
                }
            }
        })
    }
    fun starPhoto(photoId: Int, star: Int) {
//    # request parameter: photo id, whether to star or unstar the photo (1 for star, 0 for unstar)
//    # GET /starphoto?photoid=&star=
//    starPhoto(photoId: int, star: int)
        val getStarsUrl = (serverUrl+"starphoto/").toHttpUrl().newBuilder()
            .addQueryParameter("photoid", photoId.toString())
            .addQueryParameter("star", star.toString())
            .build()
        val request = Request.Builder()
            .url(getStarsUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                Log.e("starphoto", "Failed GET starphoto request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    //refresh
                }
            }
        })
    }

    fun deletePhoto(username: String, albumname: String, foldername: String, photoId: Int) {
//    # request parameter: user name, album name, folder name, photo id to delete
//    # GET /deletephoto?username=&albumname=&foldername=&photoid=
//    deletePhoto(username: str, albumname: str, foldername: str, photoId: int)
        val getDeletePhotoUrl = (serverUrl+"deletephoto/").toHttpUrl().newBuilder()
            .addQueryParameter("username", username)
            .addQueryParameter("albumname", albumname)
            .addQueryParameter("foldername", foldername)
            .addQueryParameter("photoid", photoId.toString())
            .build()
        val request = Request.Builder()
            .url(getDeletePhotoUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                Log.e("deletephoto", "Failed GET DeletePhoto request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    getImages(username,albumname,foldername)
                }
            }
        })
    }

    fun scorePhoto(username: String, albumname: String, foldername: String, photoId: Int, score:Int){
//    # input: user name, album name, folder name. photo id to delete, score to replace, new folder name to replace
//    # POST /editphoto?username=&albumname=&foldername=&photoid=&score=&newfoldername=
//    editPhoto(username: str, albumname: str, foldername: str, photoId: int, score: int, newfoldername: str)

        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("albumname", albumname)
            .addFormDataPart("foldername", foldername)
            .addFormDataPart("photoid", photoId.toString())
            .addFormDataPart("score", score.toString())
            .build()
        val request = Request.Builder()
            .url(serverUrl+"editphoto/")
            .post(mpFD)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                Log.e("scorephoto", "Failed GET scorePhoto request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    getImages(username,albumname,foldername)
                }
            }
        })

    }


    fun relocatePhoto(username: String, albumname: String, foldername: String, photoId: Int, location:String){
//    # input: user name, album name, folder name. photo id to delete, score to replace, new folder name to replace
//    # POST /editphoto?username=&albumname=&foldername=&photoid=&score=&newfoldername=
//    editPhoto(username: str, albumname: str, foldername: str, photoId: int, score: int, newfoldername: str)

        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("albumname", albumname)
            .addFormDataPart("foldername", foldername)
            .addFormDataPart("photoid", photoId.toString())
            .addFormDataPart("newfoldername", location)
            .build()
        val request = Request.Builder()
            .url(serverUrl+"editphoto/")
            .post(mpFD)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                Log.e("relocatephoto", "Failed GET relocatephoto request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    getImages(username,albumname,foldername)
                }
            }
        })

    }
}