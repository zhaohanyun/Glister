package cn.edu.sjtu.glister.glisterfrontend

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import cn.edu.sjtu.glister.glisterfrontend.databinding.ListitemImageBinding
import coil.load
import kotlinx.android.synthetic.main.activity_view.*

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
            listItemView.score.text = score.toString()

            listItemView.save.setOnClickListener()
            {
                println("bSave clicked!")

                val draw = listItemView.chattImage.drawable as BitmapDrawable
                val bitmap = draw.bitmap

                MediaStore.Images.Media.insertImage(
                    context.getContentResolver(),
                    bitmap!!,
                    "image_file",
                    "file")

            }
            listItemView.star.setOnClickListener {
                listItemView.star.setBackgroundColor(context.getResources().getColor(R.color.purple_200))
            }
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