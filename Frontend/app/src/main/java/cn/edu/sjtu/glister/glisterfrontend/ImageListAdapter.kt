package cn.edu.sjtu.glister.glisterfrontend

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.lang.System.load
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors


class ImageListAdapter(context: Context, images: List<Image>) :
    ArrayAdapter<Image>(context, 0, images) {

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.listitem_image, parent, false)
            rowView.tag = ListitemImageBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ListitemImageBinding


        getItem(position)?.run {

            //listItemView.scoreTextView.text = username
            listItemView.score.text = score.toString()
            //listItemView.score.text = photoUri.toString()
            if (isRecommended) listItemView.ifRecommended.text = "Recommended"
            else listItemView.ifRecommended.text = ""
            if (isStarred) listItemView.star.text = "UNSTAR"
            else listItemView.star.text = "STAR"

            /*listItemView.save.setOnClickListener()
            {

                println("bSave clicked!")

                val draw = listItemView.chattImage.drawable as BitmapDrawable
                val bitmap = draw.bitmap

                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    bitmap!!,
                    "image_file",
                    "file")

            }*/

            //用saveactivity的方法
            /*listItemView.save.setOnClickListener { v: View ->
                if (v.id == R.id.save) {
                    val intent = Intent(context, SaveActivity::class.java)
                    intent.putExtra("IMAGE_URI", Uri.parse(photoUri))
                    context.startActivity(intent)
                    /*println("bSave clicked!")
                    MediaStore.Images.Media.insertImage(
                        context.getContentResolver(),
                        bitmap!!,
                        "image_file",
                        "file")*/
                }
            }*/

            listItemView.star.setOnClickListener {
                if (listItemView.star.text == "UNSTAR") listItemView.star.text = "STAR"
                else listItemView.star.text = "UNSTAR"
                //listItemView.star.setBackgroundColor(context.getResources().getColor(R.color.purple_200))
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

            //直接设置方法
            listItemView.save.setOnClickListener()
            {
                val executor = Executors.newSingleThreadExecutor()

                //找个有图的网址做演示
                //用executor
                /*executor.execute {
                    val url = "https://106.14.1.108/uploads/Hanyun/20220726_010101/cat/Hanyun_20220726_010101_cat_0.jpg"
                    val bytes = URL(url).readBytes()
                    File("image.jpg").writeBytes(bytes)
                    /*runOnUiThread {
                        // update UI
                    }*/
                }*/
                //不用executor
                val url = "https://106.14.1.108/uploads/Hanyun/20220726_010101/cat/Hanyun_20220726_010101_cat_0.jpg"
                val bytes = URL(url).readBytes()
                File("image.jpg").writeBytes(bytes)
            }


        }

        return listItemView.root
    }
}

