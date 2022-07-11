//take video
package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.`activity_camera.xml`)
//    }
//}


import android.app.Activity
import android.content.Intent

//import android.support.v7.app.AppCompatActivity
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult


import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : AppCompatActivity() {

    private val VIDEO_CAPTURE = 101
    private lateinit var startForRecordResult: ActivityResultLauncher<Intent>
    private lateinit var videoView:VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        videoView=findViewById(R.id.videoView)
        recordButton.isEnabled = hasCamera()

        //set up media controller
        val mediaCollection=MediaController(this)
        mediaCollection.setAnchorView(videoView)
        videoView.setMediaController(mediaCollection)

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
                if (!it.value) {
                    toast("${it.key} access denied")
                    finish()
                }
            }
        }.launch(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE))

        startForRecordResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            val videoUri = result.data?.data
            if (result.resultCode == Activity.RESULT_OK) {

                videoView.setVideoURI(videoUri)
                videoView.start()//https://www.youtube.com/watch?v=XV0SZyy0Xis

                Toast.makeText(this, "Video saved to:\n"
                        + videoUri, Toast.LENGTH_LONG).show()
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to record video",
                    Toast.LENGTH_LONG).show()
            }
        }

    }

    fun startRecording(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        println("hello")
        //startActivityForResult(intent, VIDEO_CAPTURE)已弃用
        startForRecordResult.launch(intent)
    }

    private fun hasCamera(): Boolean {
        return packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA_ANY)
    }

// onActivityResult已弃用
//    override fun onActivityResult(requestCode: Int,
//                                  resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val videoUri = data?.data
//
//        if (requestCode == VIDEO_CAPTURE) {
//            if (resultCode == Activity.RESULT_OK) {
//                Toast.makeText(this, "Video saved to:\n"
//                        + videoUri, Toast.LENGTH_LONG).show()
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(this, "Video recording cancelled.",
//                    Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(this, "Failed to record video",
//                    Toast.LENGTH_LONG).show()
//            }
//        }
//    }

}

