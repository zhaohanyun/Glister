package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.images
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.getImages
import cn.edu.sjtu.glister.glisterfrontend.databinding.ActivityImagesBinding

class ImageActivity : AppCompatActivity() {
    private lateinit var view: ActivityImagesBinding
    private lateinit var imageListAdapter: ImageListAdapter

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
        view = ActivityImagesBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        imageListAdapter = ImageListAdapter(this, images)
        view.imageListView.setAdapter(imageListAdapter)
        images.addOnListChangedCallback(propertyObserver)

        // setup refreshContainer here later
        view.refreshContainer.setOnRefreshListener {
            refreshTimeline()
        }

        refreshTimeline()
        //getImages()
        GetPermission()
    }
    override fun onDestroy() {
        super.onDestroy()

        images.removeOnListChangedCallback(propertyObserver)
    }
    private val propertyObserver = object: ObservableList.OnListChangedCallback<ObservableArrayList<Int>>() {
        override fun onChanged(sender: ObservableArrayList<Int>?) { }
        override fun onItemRangeChanged(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeInserted(
            sender: ObservableArrayList<Int>?,
            positionStart: Int,
            itemCount: Int
        ) {
            println("onItemRangeInserted: $positionStart, $itemCount")
            runOnUiThread {
                imageListAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }
    private fun refreshTimeline() {
        getImages("Hanyun","20220725_055244","cat")

        // stop the refreshing animation upon completion:
        view.refreshContainer.isRefreshing = false
    }
}