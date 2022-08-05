//take video
package cn.edu.sjtu.glister.glisterfrontend

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.`activity_camera.xml`)
//    }
//}


//import android.support.v7.app.AppCompatActivity


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.lifecycle.ViewModel
import cn.edu.sjtu.glister.glisterfrontend.AlbumStore.postAlbum
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.activity_record.*
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RecordActivity : AppCompatActivity() {
    fun startEdit(view: View?) = startActivity(Intent(this, EditActivity::class.java))
    private val VIDEO_CAPTURE = 101
    private lateinit var startForRecordResult: ActivityResultLauncher<Intent>
    private lateinit var forPickedResult: ActivityResultLauncher<String>
    private lateinit var startForAudioResult: ActivityResultLauncher<Intent>
    private lateinit var videoView:VideoView
    private val viewState: PostViewState by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        videoView=findViewById(R.id.videoView)
        recordButton.isEnabled = hasCamera()

        //test
//        val info = CameraInfo()
//        Camera.getCameraInfo(0, info)
//        val ori=info.orientation

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
                viewState.videoUri=videoUri
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to record video",
                    Toast.LENGTH_LONG).show()
            }
        }

        forPickedResult =
            registerForActivityResult(ActivityResultContracts.GetContent(), fun(uri: Uri?) {
                uri?.let {
                    if (it.toString().contains("image"))
                    {
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

                        //doCrop(cropIntent)
                    }else
                    {
                        val inStream = contentResolver.openInputStream(it) ?: return
                        viewState.videoUri = mediaStoreAlloc("video/mp4")
                        viewState.videoUri?.let {
                            val outStream = contentResolver.openOutputStream(it) ?: return
                            val buffer = ByteArray(8192)
                            var read: Int
                            while (inStream.read(buffer).also { read = it } != -1) {
                                outStream.write(buffer, 0, read)
                            }
                            outStream.flush()
                            outStream.close()
                            inStream.close()
                        }
                        viewState.videoIcon = android.R.drawable.presence_video_busy
                        videoView.setVideoURI(viewState.videoUri)
                        videoView.start()
                        //viewState.videoUri=it
                        //view.videoButton.setImageResource(viewState.videoIcon)
                    }
                } ?: run { Log.d("Pick media", "failed") }
            })


        startForAudioResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == RESULT_OK)
            {
                val text = (result.data)?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                //what's in text?
                //txvResult.text = text[0]  //txvResult is the id of TextView
                Toast.makeText(this@RecordActivity, "You said : " + (text?.get(0) ?: "nothing"), Toast.LENGTH_SHORT).show()
                setObjectFocus(text?.get(0))
            }
        }


        navigation.setOnNavigationItemSelectedListener{ item ->
        //NavigationBarView.OnItemSelectedListener { item ->
            //println("hello object")
            when(item.itemId) {
                R.id.object_focus -> {
                    // Respond to navigation item 1 click
                    val button = findViewById<BottomNavigationItemView>(R.id.object_focus)
                    val popupMenu: PopupMenu = PopupMenu(this,button)
                    popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { obj ->
                        when(obj.itemId) {
                            R.id.object_flowers ->{
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                                setObjectFocus("flower")
                            }
                            R.id.object_faces ->{
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                                setObjectFocus("face")
                            }
                            R.id.object_cars ->{
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                                setObjectFocus("car")
                            }
                            R.id.object_cats ->{
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                                setObjectFocus("cat")
                            }
                            R.id.audio_input ->{
                            //TODO  trigger activity
                                Toast.makeText(this@RecordActivity, "You Clicked : " + obj.title, Toast.LENGTH_SHORT).show()
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

                                if (intent.resolveActivity(packageManager) != null) {
                                    startForAudioResult.launch(intent)
                                } else {
                                    Toast.makeText(this, "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        true
                    })
                    // show icons on popup menu
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        popupMenu.setForceShowIcon(true)
                    }else {
                        try {
                            val fields = popupMenu.javaClass.declaredFields
                            for (field in fields) {
                                if ("mPopup" == field.name) {
                                    field.isAccessible = true
                                    val menuPopupHelper = field.get(popupMenu)
                                    val classPopupHelper =
                                        Class.forName(menuPopupHelper.javaClass.name)
                                    val setForceIcons: Method = classPopupHelper.getMethod(
                                        "setForceShowIcon",
                                        Boolean::class.javaPrimitiveType
                                    )
                                    setForceIcons.invoke(menuPopupHelper, true)
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    popupMenu.show()
                    true

                }
                R.id.upload -> {
                    // Respond to navigation item 2 click
                    forPickedResult.launch("*/*")
                    true
                }
                R.id.profile -> {
                    // Respond to navigation item 2 click
                    val intent = Intent (applicationContext, AlbumFolderActivity::class.java)
                    intent.putExtra("username","Hanyun")
                    this.startActivity(intent) //go to album folder page
                    true
                }
                else -> false
            }
        }


    }

    fun startRecording(view: View) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaStoreAlloc("video/mp4"))
        //startActivityForResult(intent, VIDEO_CAPTURE)已弃用
        startForRecordResult.launch(intent)
    }

    private fun hasCamera(): Boolean {
        return packageManager.hasSystemFeature(
            PackageManager.FEATURE_CAMERA_ANY)
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

    private fun setObjectFocus(obj:String?){
        //TODO: determine whether obj is in our set
        viewState.objectFocus=obj
    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu?.apply {
//            add(Menu.NONE, Menu.FIRST, Menu.NONE, getString(R.string.send))
//            getItem(0).setIcon(android.R.drawable.ic_menu_send).setEnabled(viewState.enableSend)
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
//        }
//        return super.onPrepareOptionsMenu(menu)
//    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.apply {
            add(Menu.NONE, Menu.FIRST, Menu.NONE, getString(R.string.send))
            getItem(0).setIcon(android.R.drawable.ic_menu_send)
                .setEnabled(viewState.enableSend)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == Menu.FIRST) {
            viewState.enableSend = false
            invalidateOptionsMenu()
            submitAlbum(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun submitAlbum(activity: Activity) {
        val album = Album(username = "Hanyun",
            albumname = genAlbumName())

        postAlbum(applicationContext, album, viewState.imageUri, viewState.videoUri,activity,viewState.objectFocus) { msg ->
            runOnUiThread {
                toast(msg)
            }
        }
        viewState.enableSend=true

    }

    private fun genAlbumName(): String? {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val formatted = current.format(formatter)
        println(formatted)
        return formatted
    }

    //private fun startPost(view: View?) = startActivity(Intent(this, ImageActivity::class.java))
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


class PostViewState: ViewModel() {
    var enableSend = true
    var imageUri: Uri? = null
    var videoUri: Uri? = null
    var videoIcon = android.R.drawable.presence_video_online
    var objectFocus:String?=null
}

class Album(var username: String? = null,
            var albumname: String? = null)

//class ObjectFolder:Album(){ //This type is final, so it cannot be inherited from
//    var objectname: String?=null
//}

class ObjectFolder(var username: String? = null,
                   var albumname: String? = null,
                   var objectname: String?=null)


//kind tip from Hanyun:
//key to any emulator problem:
//taskkill /F /IM "qemu-system-x86_64.exe" /T