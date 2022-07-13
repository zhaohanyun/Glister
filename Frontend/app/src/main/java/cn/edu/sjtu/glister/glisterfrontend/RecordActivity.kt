//take video
package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : AppCompatActivity() {
    fun startEdit(view: View?) = startActivity(Intent(this, EditActivity::class.java))
    fun startView(view: View?) = startActivity(Intent(this, ViewImages::class.java))
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

        navigation.setOnNavigationItemSelectedListener{ item ->
        //NavigationBarView.OnItemSelectedListener { item ->
            println("hello object")
            when(item.itemId) {
                R.id.object_focus -> {
                    // Respond to navigation item 1 click
                    val button = findViewById<BottomNavigationItemView>(R.id.object_focus)
                    val popupMenu: PopupMenu = PopupMenu(this,button)
                    popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { obj ->
                        when(obj.itemId) {
                            R.id.object_flowers ->
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                            R.id.object_faces ->
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                            R.id.object_cars ->
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                        }
                        true
                    })
                    popupMenu.show()
                    true
                }
                R.id.upload -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.profile -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

    }

    fun startRecording(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
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

