package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.databinding.ObservableArrayList
/*import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue*/
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties


object ImageStore {

    private val _images = arrayListOf<Image>()
    //val chatts: List<Chatt> = _chatts
    private val nFields = Image::class.declaredMemberProperties.size

    //private lateinit var queue: RequestQueue
    //private const val serverUrl ="https://52.39.198.75/"
    private val serverUrl : String=  Resources.getSystem().getString(R.string.Hanyun_server)
    private val client = OkHttpClient()


    val images = ObservableArrayList<Image>()

    fun getImages() {
        val request = Request.Builder()
            .url(serverUrl+"getimages/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getImages", "Failed GET request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val imagesReceived = try { JSONObject(response.body?.string() ?: "").getJSONArray("images") } catch (e: JSONException) { JSONArray() }

                    images.clear()
                    for (i in 0 until imagesReceived.length()) {
                        val imageEntry = imagesReceived[i] as JSONArray
                        if (imageEntry.length() == nFields) {
                            images.add(Image(score = imageEntry[0].toString(),
                                imageUrl = imageEntry[1].toString(),
                            ))
                        } else {
                            Log.e("getImages", "Received unexpected number of fields " + imageEntry.length().toString() + " instead of " + nFields.toString())
                        }
                    }
                }
            }
        })
    }
}