package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import cn.edu.sjtu.glister.glisterfrontend.databinding.ListitemImageBinding
import coil.load

class ImageListAdapter(context: Context, images: List<Image>) :
    ArrayAdapter<Image>(context, 0, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.listitem_image, parent, false)
            rowView.tag = ListitemImageBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemImageBinding

        getItem(position)?.run {
            //listItemView.scoreTextView.text = username
            listItemView.root.setBackgroundColor(Color.parseColor(if (position % 2 == 0) "#E0E0E0" else "#EEEEEE"))
            photoUri?.let {
                listItemView.chattImage.setVisibility(View.VISIBLE)
                listItemView.chattImage.load(it) {
                    crossfade(true)
                    crossfade(1000)
                }
            } ?: run {
                listItemView.chattImage.setVisibility(View.GONE)
                listItemView.chattImage.setImageBitmap(null)
            }

        }

        return listItemView.root
    }
}