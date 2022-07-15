package cn.edu.sjtu.glister.glisterfrontend

import android.content.Intent
import android.graphics.Color
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
        getImages("Hanyun","20220715_121710","cat")

        // stop the refreshing animation upon completion:
        view.refreshContainer.isRefreshing = false
    }
}