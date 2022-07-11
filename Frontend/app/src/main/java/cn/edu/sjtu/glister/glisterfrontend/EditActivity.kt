package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import cn.edu.sjtu.glister.glisterfrontend.databinding.ActivityEditBinding

class EditViewState: ViewModel() {
    var imageUri: Uri? = null
}

class EditActivity : AppCompatActivity() {
    private lateinit var view: cn.edu.sjtu.glister.glisterfrontend.databinding.ActivityEditBinding
    private val viewState: EditViewState by viewModels()
    private lateinit var forCropResult: ActivityResultLauncher<Intent>

    private fun initCropIntent(): Intent? {
        // Is there any published Activity on device to do image cropping?
        val intent = Intent("com.android.camera.action.CROP")
        intent.type = "image/*"
        val listofCroppers = packageManager.queryIntentActivities(intent, 0)
        // No image cropping Activity published
        if (listofCroppers.size == 0) {
            toast("Device does not support image cropping")
            return null
        }

        intent.component = ComponentName(
            listofCroppers[0].activityInfo.packageName,
            listofCroppers[0].activityInfo.name)

        // create a square crop box:
        intent.putExtra("outputX", 500)
            .putExtra("outputY", 500)
            .putExtra("aspectX", 1)
            .putExtra("aspectY", 1)
            // enable zoom and crop
            .putExtra("scale", true)
            .putExtra("crop", true)
            .putExtra("return-data", true)

        return intent
    }

    private fun doCrop(intent: Intent?) {
        intent ?: run {
            viewState.imageUri?.let { view.previewImage.display(it) }
            return
        }

        viewState.imageUri?.let {
            intent.data = it
            forCropResult.launch(intent)
        }
    }

    private fun mediaStoreAlloc(mediaType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.MIME_TYPE, mediaType)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        return contentResolver.insert(
            if (mediaType.contains("video"))
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = ActivityEditBinding.inflate(layoutInflater)
        setContentView(view.root)

        viewState.imageUri?.let { view.previewImage.display(it) }
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        val launcher = registerForActivityResult(contract) { results ->
            results.forEach { result ->
                if (!result.value) {
                    toast("${result.key} access denied")
                    finish()
                }
            }
        }
        forCropResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data.let {
                        viewState.imageUri?.run {
                            if (!toString().contains("ORIGINAL")) {
                                // delete uncropped photo taken for posting
                                contentResolver.delete(this, null, null)
                            }
                        }
                        viewState.imageUri = it
                        viewState.imageUri?.let { view.previewImage.display(it) }
                    }
                } else {
                    Log.d("Crop", result.resultCode.toString())
                }
            }

        launcher.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE))
        val cropIntent = initCropIntent()
        val forPickedResult =
            registerForActivityResult(ActivityResultContracts.GetContent(), fun(uri: Uri?) {
                uri?.let {
                    val inStream = contentResolver.openInputStream(it) ?: return
                    viewState.imageUri = mediaStoreAlloc("image/jpeg")
                    viewState.imageUri?.let {
                        val outStream = contentResolver.openOutputStream(it) ?: return
                        val buffer = ByteArray(8192)
                        var read: Int
                        while (inStream.read(buffer).also{ read = it } != -1) {
                            outStream.write(buffer, 0, read)
                        }
                        outStream.flush()
                        outStream.close()
                        inStream.close()
                    }
                    doCrop(cropIntent)
                } ?: run { Log.d("Pick media", "failed") }
            })
        view.albumButton.setOnClickListener {
            forPickedResult.launch("*/*")
        }
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            toast("Device has no camera!")
            return
        }
    }
}