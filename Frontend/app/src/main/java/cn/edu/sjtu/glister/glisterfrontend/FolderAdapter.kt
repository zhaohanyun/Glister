package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import cn.edu.sjtu.glister.glisterfrontend.databinding.ListitemFolderBinding

class FolderAdapter(context: Context, foldernames: ArrayList<Album>) :
    ArrayAdapter<Album>(context, 0, foldernames){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.listitem_folder, parent, false)
            rowView.tag = ListitemFolderBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemFolderBinding

        getItem(position)?.run{
            listItemView.albumButton.text=albumname

            albumname?.let {
                listItemView.albumButton.setOnClickListener { v: View ->
                    if (v.id == R.id.albumButton) {
                        val intent = Intent (context, ImageActivity::class.java)
                        intent.putExtra("username","Hanyun")
                        intent.putExtra("albumname",albumname)
                        context.startActivity(intent)
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