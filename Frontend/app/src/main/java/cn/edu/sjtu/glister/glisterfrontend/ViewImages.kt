package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_view.*
import java.time.temporal.TemporalAdjusters.next
import kotlin.coroutines.EmptyCoroutineContext.plus

class ViewImages : AppCompatActivity() {

    // 定义一个访问图片的数组
    private var images = intArrayOf(R.drawable.lijiang, R.drawable.qiao,
        R.drawable.shuangta, R.drawable.shui, R.drawable.xiangbi)
    // 定义默认显示的图片
    private var currentImg = 2
    // 定义图片的初始透明度
    private var alpha = 255
    fun GetPermission()
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        //val plus = findViewById<Button>(R.id.plus)
        //val minus = findViewById<Button>(R.id.minus)
        val image1 = findViewById<ImageView>(R.id.image1)
        val next = findViewById<Button>(R.id.next)
        next.setOnClickListener {
            image1.setImageResource(images[++currentImg % images.size])
        }
        // 定义改变图片透明度的方法
        val listener = View.OnClickListener {v ->
            /*if (v == plus) {
                alpha += 20
            }
            if (v == minus){
                alpha -= 20
            }*/
            if (alpha >= 255){
                alpha = 255
            }
            if (alpha <= 0){
                alpha = 0
            }
            image1.imageAlpha = alpha
        }
        //plus.setOnClickListener(listener)
        //minus.setOnClickListener(listener)
        image1.setOnTouchListener { v, event ->
            val bitmapDrawable = image1.drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            val scale = 1.0 * bitmap.height / image1.getHeight()
            var x = (event.x * scale).toInt()
            var y = (event.y * scale).toInt()
            if (x + 120 > bitmap.width){
                x = bitmap.width - 120
            }
            if (y + 120 > bitmap.height){
                y = bitmap.height - 120
            }
            false
        }
        this.Save.setOnClickListener()
        {
            println("bSave clicked!")

            val draw = this.image1.drawable as BitmapDrawable
            val bitmap = draw.bitmap

            MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap!!,
                "image_file",
                "file")

        }
        GetPermission()
    }
}