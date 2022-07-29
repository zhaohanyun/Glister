package cn.edu.sjtu.glister.glisterfrontend

import android.content.Intent.getIntent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import cn.edu.sjtu.glister.glisterfrontend.ObjectFolderStore.objectfolders
import cn.edu.sjtu.glister.glisterfrontend.databinding.ObjectFoldersBinding

class ObjectFolderActivity: AppCompatActivity() {
    private lateinit var view: ObjectFoldersBinding
    private lateinit var objectfolderAdapter: ObjectFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ObjectFoldersBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        //val ArrayofAlbumNames:ArrayList<String> = savedInstanceState?.getStringArrayList("ArrayofFolders") as ArrayList<String> //fail, savedInstanceState is null
        //val ArrayofAlbumNames:ArrayList<String> = getIntent().getExtras()?.getStringArrayList("ArrayofFolders") as ArrayList<String>
        //cannot pass List within intent
        val username:String=(getIntent().getExtras()?.getString("username"))?:""
        val albumname:String=(getIntent().getExtras()?.getString("albumname"))?:""


        objectfolderAdapter = ObjectFolderAdapter(this, objectfolders)
        view.ObjectFolderListView.setAdapter(objectfolderAdapter)
        ObjectFolderStore.objectfolders.addOnListChangedCallback(propertyObserver)

        // setup refreshContainer here later
        view.refreshContainer.setOnRefreshListener {
            refreshTimeline(username,albumname)
        }

        refreshTimeline(username,albumname)
        //getAlbums(applicationContext,username,this)
    }
    override fun onDestroy() {
        super.onDestroy()

        ImageStore.images.removeOnListChangedCallback(propertyObserver)
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
                objectfolderAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }
    private fun refreshTimeline(username:String,albumname:String) {
        ObjectFolderStore.getFolders(username, albumname)
        // stop the refreshing animation upon completion:
        view.refreshContainer.isRefreshing = false
    }
}