package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.edu.sjtu.glister.glisterfrontend.AlbumStore.deleteAlbum
import cn.edu.sjtu.glister.glisterfrontend.AlbumStore.editAlbum
import cn.edu.sjtu.glister.glisterfrontend.databinding.ListitemFolderBinding
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener


class AlbumFolderAdapter(context: Context, foldernames: ArrayList<Album>) :
    ArrayAdapter<Album>(context, 0, foldernames){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.listitem_folder, parent, false)
            rowView.tag = ListitemFolderBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemFolderBinding

        getItem(position)?.run{
            listItemView.folderButton.text=albumname
            // TODO: Incorporate EditAlbum with EditText View


            albumname?.let {
//                listItemView.folderButton.setOnClickListener { v: View ->
//                    if (v.id == R.id.folderButton) {
//                        val intent = Intent (context, ObjectFolderActivity::class.java)
//                        intent.putExtra("username","Hanyun")
//                        intent.putExtra("albumname",albumname)
//                        context.startActivity(intent)
//                    }
//                }
                listItemView.folderButton.setOnClickListener(DoubleClick(object :
                    DoubleClickListener {
                    override fun onSingleClickEvent(view: View?) {
                        val intent = Intent(context, ObjectFolderActivity::class.java)
                        intent.putExtra("username", "Hanyun")
                        intent.putExtra("albumname", albumname)
                        context.startActivity(intent)
                    }

                    override fun onDoubleClickEvent(view: View?) {
                        // DO STUFF DOUBLE CLICK
                        listItemView.folderButton.visibility = View.INVISIBLE
                        listItemView.edtTextRename.visibility = View.VISIBLE
                    }
                }))

//                listItemView.edtTextRename.setOnLongClickListener(OnLongClickListener {
//                    deleteAlbum(username?:"", albumname!!)
//                    true
//                })
                listItemView.edtTextRename.setOnClickListener(DoubleClick(object :
                    DoubleClickListener {
                    override fun onSingleClickEvent(view: View?) {
                        // DO STUFF SINGLE CLICK
                        var newAlbumName: String = listItemView.edtTextRename.text.toString()
                        if (newAlbumName.isEmpty()) {
                            newAlbumName = "UNTITLED"
                        }
                        println(newAlbumName)
                        //listItemView.folderButton.text = newAlbumName
                        listItemView.folderButton.visibility = View.VISIBLE
                        listItemView.edtTextRename.visibility = View.INVISIBLE
                        editAlbum(username ?: "", it, newAlbumName)
                    }

                    override fun onDoubleClickEvent(view: View?) {
                        // DO STUFF DOUBLE CLICK
                        listItemView.edtTextRename.visibility = View.VISIBLE
                        listItemView.folderButton.visibility = View.INVISIBLE
                        listItemView.edtTextRename.setBackgroundColor(Color.parseColor("@color/purple_200"))
                        //cannot set background color in xml, or will destroy databinding!
                    }
                }))

                listItemView.deleteAlbum.setOnClickListener { v: View ->
                    if (v.id == R.id.delete_album) {
                        deleteAlbum(username ?: "", it)
                    }
                }
            }
        }
        return listItemView.root

//        getItem(position)?.run {
//            listItemView.albumButton.text = username
//
//            //listItemView.root.setBackgroundColor(Color.parseColor(if (position % 2 == 0) "#E0E0E0" else "#EEEEEE"))
//
//            // show image
//            imageUrl?.let {
//                listItemView.chattImage.setVisibility(View.VISIBLE)
//                listItemView.chattImage.load(it) {
//                    crossfade(true)
//                    crossfade(1000)
//                }
//            } ?: run {
//                listItemView.chattImage.setVisibility(View.GONE)
//                listItemView.chattImage.setImageBitmap(null)
//            }
//
//            videoUrl?.let {
//                listItemView.videoButton.visibility = View.VISIBLE
//                listItemView.videoButton.setOnClickListener { v: View ->
//                    if (v.id == R.id.videoButton) {
//                        val intent = Intent(context, VideoPlayActivity::class.java)
//                        intent.putExtra("VIDEO_URI", Uri.parse(it))
//                        context.startActivity(intent)
//                    }
//                }
//            } ?: run {
//                listItemView.videoButton.visibility = View.INVISIBLE
//                listItemView.videoButton.setOnClickListener(null)
//            }
//        }
//
//        return listItemView.root
    }
}


class ObjectFolderAdapter(context: Context, foldernames: ArrayList<ObjectFolder>) :
    ArrayAdapter<ObjectFolder>(context, 0, foldernames) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView =
                LayoutInflater.from(context).inflate(R.layout.listitem_folder, parent, false)
            rowView.tag = ListitemFolderBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemFolderBinding

        getItem(position)?.run {
            listItemView.folderButton.text = objectname
            listItemView.deleteAlbum.visibility= GONE

            objectname?.let {
//                if(it!="myfavorites") {
//                    listItemView.folderButton.setOnClickListener { v: View ->
//                        if (v.id == R.id.folderButton) {
//                            val intent = Intent(context, ImageActivity::class.java)
//                            intent.putExtra("username", "Hanyun")
//                            intent.putExtra("albumname", albumname)
//                            intent.putExtra("objectname", objectname)
//                            context.startActivity(intent)
//                        }
//                    }
//                }else {
//                    listItemView.folderButton.setOnClickListener { v: View ->
//                        val intent = Intent(context, ImageActivity::class.java)
//                        intent.putExtra("username", "Hanyun")
//                        intent.putExtra("albumname", "myfavorites")
//                        context.startActivity(intent)
//                    }
//                }
                listItemView.folderButton.setOnClickListener { v: View ->
                    if (v.id == R.id.folderButton) {
                        val intent = Intent(context, ImageActivity::class.java)
                        intent.putExtra("username", "Hanyun")
                        intent.putExtra("albumname", albumname)
                        intent.putExtra("objectname", objectname)
                        context.startActivity(intent)
                    }
                }
            }
        }
        return listItemView.root
    }
}