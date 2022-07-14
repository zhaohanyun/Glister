package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.net.toFile
import androidx.databinding.ObservableArrayList
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import java.io.IOException
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object AlbumStore {
//    private val _chatts = arrayListOf<Chatt>()
//    val chatts = ObservableArrayList<Chatt>()
//    private val nFields = Chatt::class.declaredMemberProperties.size

    //private const val serverUrl ="https://52.39.198.75/"
    private val serverUrl : String=  Resources.getSystem().getString(R.string.Hanyun_server)
    private val client = OkHttpClient()


    fun postAlbum(
        context: Context, chatt: Chatt, imageUri: Uri?, videoUri: Uri?, //chatt will be replaced later
        completion: (String) -> Unit
    )
    //        # Upload video to the server, and create a new album.
    //        # input: username, album name, the uploaded video
    //        # output: a list of albums of this user
    //        # POST /postalbum?username=&albumname=&image=&video=
    {
        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", chatt.username ?: "")
            .addFormDataPart("message", chatt.message ?: "")

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
            .url(serverUrl + "postimages/")
            .post(mpFD.build())
            .build()

        context.toast("Posting . . . wait for 'Chatt posted!'")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(e.localizedMessage ?: "Posting failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    //getChatts()
                    completion("Chatt posted!")
                }
            }

        })
    }

    fun getAlbums()
//        # input: username
//        # output: a list of albums of this user (e.g. Jay Chou concert, NBA live)
//        # GET /getalbums?username=&albumname=
//        getAlbums(username: str) -> list[str]
    {

    }
}