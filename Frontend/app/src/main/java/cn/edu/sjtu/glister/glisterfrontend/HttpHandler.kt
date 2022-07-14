package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableArrayList
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.declaredMemberProperties


object AlbumStore {
    private val _albums = arrayListOf<Album>()
    val albums = ObservableArrayList<Album>()
    private val nFields = Chatt::class.declaredMemberProperties.size

    //private const val serverUrl ="https://52.39.198.75/"
    //private val serverUrl : String=  Resources.getSystem().getString(R.string.Hanyun_server)
    //private val serverUrl : String=  Resources.getSystem().getString(R.string.server)
    private const val serverUrl ="https://106.14.1.108/"
    private val client = OkHttpClient.Builder()
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()


    fun postAlbum(
        context: Context, album: Album, imageUri: Uri?, videoUri: Uri?, //chatt will be replaced later
        completion: (String) -> Unit
    )
    //        # Upload video to the server, and create a new album.
    //        # input: username, album name, the uploaded video
    //        # output: a list of albums of this user
    //        # POST /postalbum?username=&albumname=&image=&video=
    {
        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", album.username ?: "")
            .addFormDataPart("albumname", album.albumname ?: "")

        var falsefile:Boolean = false

        imageUri?.run {
            toFile(context)?.let {
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
            toFile(context)?.let {
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
                    getAlbums(album.username?:"") //I guess we cannot get immediately after post
                    completion("new Album posted!")
                }
            }

        })
    }

    fun getAlbums(username:String)
//        # input: username
//        # output: a list of albums of this user (e.g. Jay Chou concert, NBA live)
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
                    for (i in 0 until albumsReceived.length()) {
                        println(albumsReceived[i])
                    }
                    //TODO: use these name to generate view of album folders
                }

            }
        })
    }

    fun getFolders()
//    # input: username, album to open
//    # output: a list of foldernames in the album
//    # GET /getfolders?username=&albumname=
//    getFolders(username: str, albumname: str) -> list[str]
    {

    }
}