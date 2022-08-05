package cn.edu.sjtu.glister.glisterfrontend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

import androidx.databinding.ObservableArrayList
import cn.edu.sjtu.glister.glisterfrontend.AlbumStore.albums
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties

const val serverUrl ="https://106.14.1.108/"

object AlbumStore {
    private val _albums = arrayListOf<Album>()
    val albums = ObservableArrayList<Album>()

    //private const val serverUrl ="https://52.39.198.75/"
    //private val serverUrl : String=  Resources.getSystem().getString(R.string.Hanyun_server)
    //private val serverUrl : String=  Resources.getSystem().getString(R.string.server)
    //private const val serverUrl ="https://106.14.1.108/"
    private val client = OkHttpClient.Builder()
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()


    fun postAlbum(
        context: Context, album: Album, imageUri: Uri?, videoUri: Uri?,activity: Activity, objectFocus:String?,
        completion: (String) -> Unit
    )
        //    # Upload video to the server, and create a new album.
        //    # input: username, album name, object focus (optional), the uploaded video
        //    # POST /postalbum?username=&albumname=&focus=&image=&video=
    {
        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", album.username ?: "")
            .addFormDataPart("albumname", album.albumname ?: "")

        objectFocus?.let {
            mpFD.addFormDataPart("focus",objectFocus)
        }

        var falsefile:Boolean = false

        imageUri?.run {
            toFile(context,this)?.let {
                mpFD.addFormDataPart(
                    "image", "albumImage",
                    it.asRequestBody("image/jpeg".toMediaType())
                )
            } ?: run{
                //context.toast("Unsupported image format")
                falsefile=true
            }
        }

        videoUri?.run {
            toFile(context,this)?.let {
                mpFD.addFormDataPart(
                    "video", "albumVideo",
                    it.asRequestBody("video/mp4".toMediaType())
                )
            } ?: run{
                //context.toast("Unsupported video format")
                falsefile=true
            }


//        } ?: unsupported@{
//            context.toast("Unsupported video format")
//            return@unsupported
//        } //not working
        }

        if (falsefile) return

        val request = Request.Builder()
            .url(serverUrl + "postalbum/")
            .post(mpFD.build())
            .build()

        context.toast("Posting . . . wait for 'Album posted!'")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(e.localizedMessage ?: "Posting failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    //getChatts()
                    //getAlbums(album.username?:"") //I guess we cannot get immediately after post

                    completion("new Album posted!")
                    val intent = Intent (context, AlbumFolderActivity::class.java)
                    intent.putExtra("username","Hanyun")
                    activity.startActivity(intent) //go to album folder page

//                    val intent = Intent (context, ImageActivity::class.java) //go to image folder page
//                    //val intent = Intent (context, ViewImages::class.java)
//                    activity.startActivity(intent)
                }
            }

        })
    }

    fun getAlbums(username:String)
//        # request parameter: username
//        # response: a list of albums of this user (e.g. Jay Chou concert, NBA live)
//        # GET /getalbums?username=&albumname=
//        getAlbums(username: str) -> list[str]
    {
        val getAlbumsUrl = (serverUrl+"getalbums/").toHttpUrl().newBuilder()
            .addQueryParameter("username", username)
            .build()
        val request = Request.Builder()
            .url(getAlbumsUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getAlbums", "Failed GET album request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val albumsReceived = try { JSONObject(response.body?.string() ?: "").getJSONArray("albums") } catch (e: JSONException) { JSONArray() }
                    //albumsReceived is a list of albumnames
                    println("You have albums:")
                    albums.clear()
                    for (i in 0 until albumsReceived.length()) {
                        println(albumsReceived[i])
                        albums.add(Album(username, albumsReceived[i] as String?)) //update Model
                    }
//                    val albumnames = Array(albumsReceived.length()) {
//                        albumsReceived.getString(it)
//                    }
                    //bad implementation!
//                    val intent = Intent (context, AlbumFolderActivity::class.java)
//                    intent.putStringArrayListExtra("ArrayofFolders",ArrayList(albumnames.toMutableList())) //ArrayList
//                    intent.putExtra("username",username)
//                    activity.startActivity(intent)
                }


            }
        })
    }


    fun editAlbum(username: String, albumname: String, newAlbumname: String){
        //    # request parameter: username, album to edit, new album name
        //    # GET /editalbum?username=&albumname=&newalbumname=
        //    editAlbum(username: str, albumname: str, newAlbumname: str)

        val editAlbumsUrl = (serverUrl+"editalbum/").toHttpUrl().newBuilder()
            .addQueryParameter("username", username)
            .addQueryParameter("albumname", albumname)
            .addQueryParameter("newalbumname", newAlbumname)
            .build()
        val request = Request.Builder()
            .url(editAlbumsUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("editalbum", "Failed GET edit album request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    getAlbums(username)
                    //use completion handler will end this activity
                }
            }
        })

    }

    fun deleteAlbum(username:String, albumname:String) {
//    # request parameter: username, album to delete
//    # GET /editalbum?username=&albumname=
//    deleteAlbum(username: str, albumname: str)

        //TODO
        val deleteAlbumsUrl = (serverUrl+"deletealbum/").toHttpUrl().newBuilder()
            .addQueryParameter("username", username)
            .addQueryParameter("albumname", albumname)
            .build()
        val request = Request.Builder()
            .url(deleteAlbumsUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("deleteAlbums", "Failed GET delete album request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    getAlbums(username)
                }else{
                    Log.e("deleteAlbums","error response")
                }
            }
        })
    }
}


object ObjectFolderStore {
    private val _objectfolders = arrayListOf<ObjectFolder>()
    val objectfolders = ObservableArrayList<ObjectFolder>()
    private val client = OkHttpClient.Builder()
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    fun getFolders(username:String, albumname:String){
//    # request parameter: username, album to open
//    # response: a list of foldernames in the album
//    # GET /getfolders?username=&albumname=
//    getFolders(username: str, albumname: str) -> list[str]

        val getFoldersUrl = (serverUrl+"getfolders/").toHttpUrl().newBuilder()
            .addQueryParameter("username", username)
            .addQueryParameter("albumname", albumname)
            .build()
        val request = Request.Builder()
            .url(getFoldersUrl)
            .build()

        ObjectFolderStore.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getObjectFolders", "Failed GET object folder request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val objectFoldersReceived = try { JSONObject(response.body?.string() ?: "").getJSONArray("folders") } catch (e: JSONException) { JSONArray() }
                    //albumsReceived is a list of albumnames
                    objectfolders.clear()
                    for (i in 0 until objectFoldersReceived.length()) {
                        println(objectFoldersReceived[i])
                        objectfolders.add(ObjectFolder(username, albumname, objectFoldersReceived[i] as String?)) //update Model
                    }
                }
            }
        })
    }

}