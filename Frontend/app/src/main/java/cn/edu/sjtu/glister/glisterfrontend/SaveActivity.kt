package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sjtu.glister.glisterfrontend.databinding.ActivitySaveBinding
import java.io.File
import java.net.URL
import java.util.concurrent.Executors

class SaveActivity : AppCompatActivity() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
    }*/

    /*fun GetPermission()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            val REQUEST_CODE_CONTACT = 101
            val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限
            for (str in permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT)
                }
            }
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //GetPermission()

        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val url = "https://106.14.1.108/uploads/Hanyun/20220726_010101/cat/Hanyun_20220726_010101_cat_0.jpg"
            val bytes = URL(url).readBytes()
            File("image.jpg").writeBytes(bytes)
            runOnUiThread {
                // update UI
            }
        }
        /*val view = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(view.root)

        view.imageView.setImageURI(intent.getParcelableExtra("VIDEO_URI"))*/



    }
}