package cn.edu.sjtu.glister.glisterfrontend

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import cn.edu.sjtu.glister.glisterfrontend.AlbumStore.albums
import cn.edu.sjtu.glister.glisterfrontend.AlbumStore.getAlbums
import cn.edu.sjtu.glister.glisterfrontend.databinding.AlbumFoldersBinding

class AlbumFolderActivity : AppCompatActivity() {
    private lateinit var view: AlbumFoldersBinding
    private lateinit var albumfolderAdapter: AlbumFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = AlbumFoldersBinding.inflate(layoutInflater)
        view.root.setBackgroundColor(Color.parseColor("#E0E0E0"))
        setContentView(view.root)

        //val ArrayofAlbumNames:ArrayList<String> = savedInstanceState?.getStringArrayList("ArrayofFolders") as ArrayList<String> //fail, savedInstanceState is null
        //val ArrayofAlbumNames:ArrayList<String> = getIntent().getExtras()?.getStringArrayList("ArrayofFolders") as ArrayList<String>
        //cannot pass List within intent
        val username:String=(getIntent().getExtras()?.getString("username"))?:""

        albumfolderAdapter = AlbumFolderAdapter(this, albums)
        view.AlbumFolderListView.setAdapter(albumfolderAdapter)
        albums.addOnListChangedCallback(propertyObserver)

        // setup refreshContainer here later
        view.refreshContainer.setOnRefreshListener {
            refreshTimeline(username)
        }

        refreshTimeline(username)
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
                albumfolderAdapter.notifyDataSetChanged()
            }
        }
        override fun onItemRangeMoved(sender: ObservableArrayList<Int>?, fromPosition: Int, toPosition: Int,
                                      itemCount: Int) { }
        override fun onItemRangeRemoved(sender: ObservableArrayList<Int>?, positionStart: Int, itemCount: Int) { }
    }
    private fun refreshTimeline(username:String) {
        //ImageStore.getImages("Hanyun", "20220715_121710", "cat") //test

        getAlbums(username)
        // stop the refreshing animation upon completion:
        view.refreshContainer.isRefreshing = false
    }
}