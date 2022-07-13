package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.net.Uri
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

object ChattStore {
//    private val _chatts = arrayListOf<Chatt>()
//    val chatts = ObservableArrayList<Chatt>()
//    private val nFields = Chatt::class.declaredMemberProperties.size

    private const val serverUrl = "https://52.39.198.75/"
    private val client = OkHttpClient()

    fun postChatt(
        context: Context, chatt: Chatt, imageUri: Uri?, videoUri: Uri?,
        completion: (String) -> Unit
    ) {
        val mpFD = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", chatt.username ?: "")
            .addFormDataPart("message", chatt.message ?: "")

        var falsefile:Boolean = false

        imageUri?.run {
            toFile(context)?.let {
                mpFD.addFormDataPart(
                    "image", "chattImage",
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
                    "video", "chattVideo",
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
}