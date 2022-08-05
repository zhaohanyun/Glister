package cn.edu.sjtu.glister.glisterfrontend

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.deletePhoto
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.getImages
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.relocatePhoto
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.scorePhoto
import cn.edu.sjtu.glister.glisterfrontend.ImageStore.starPhoto
import cn.edu.sjtu.glister.glisterfrontend.databinding.ListitemImageBinding
import coil.load
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


class ImageListAdapter(context: Context, images: List<Image>,username:String,albumname:String,foldername:String) :
    ArrayAdapter<Image>(context, 0, images) {

    private val username=username
    private val albumname=albumname
    private val foldername=foldername

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
//                if (listItemView.star.text == "UNSTAR") listItemView.star.text = "STAR"
//                else listItemView.star.text = "UNSTAR"
                //listItemView.star.setBackgroundColor(context.getResources().getColor(R.color.purple_200))
                starPhoto(photoId?:-1,if(isStarred)0 else 1)
                getImages(username,albumname,foldername)
            }
            listItemView.delete.setOnClickListener {
                if (photoId != null) {
                    deletePhoto(username,albumname,foldername,photoId)
                }
                getImages(username,albumname,foldername)
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
            listItemView.save.setOnClickListener() {
                //val executor = Executors.newSingleThreadExecutor()

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
                //val url = "https://106.14.1.108/uploads/Hanyun/20220726_010101/cat/Hanyun_20220726_010101_cat_0.jpg"
//                val url = URL(photoUri)
//                val bytes = url.readBytes()
//                File("image.jpg").writeBytes(bytes)

                var mImage: Bitmap?
                val myExecutor = Executors.newSingleThreadExecutor()
                val myHandler = Handler(Looper.getMainLooper())
                myExecutor.execute {
                    mImage = mLoad(URL(photoUri))
                    myHandler.post {
                        mImage?.let { mSaveMediaToStorage(it) }
                    }
                }

            }

            //editscore
            listItemView.score.setOnClickListener(DoubleClick(object :
                DoubleClickListener {
                override fun onSingleClickEvent(view: View?) {
                }
                override fun onDoubleClickEvent(view: View?) {
                    // DO STUFF DOUBLE CLICK
                    listItemView.score.visibility = View.INVISIBLE
                    listItemView.editscore.visibility = View.VISIBLE
                }
            }))

            listItemView.editscore.setOnClickListener(DoubleClick(object :
                DoubleClickListener {
                override fun onSingleClickEvent(view: View?) {
                    // DO STUFF SINGLE CLICK
                    var newScore: String = listItemView.editscore.text.toString()
                    if (newScore.isEmpty()) {
                        newScore = "UNTITLED"
                    }
                    println(newScore)
                    //listItemView.folderButton.text = newAlbumName
                    listItemView.score.visibility = View.VISIBLE
                    listItemView.editscore.visibility = View.INVISIBLE
                    if (photoId != null) {
                        scorePhoto(username,albumname,foldername,photoId, newScore.toInt())
                    }
                }

                override fun onDoubleClickEvent(view: View?) {
                    // DO STUFF DOUBLE CLICK
                    listItemView.editscore.visibility = View.VISIBLE
                    listItemView.score.visibility = View.INVISIBLE
                }
            }))


            //edit location
            listItemView.relocate.setOnClickListener{
                    listItemView.relocate.visibility = View.INVISIBLE
                    listItemView.relocateDst.visibility = View.VISIBLE
            }


            listItemView.relocateDst.setOnClickListener(DoubleClick(object :
                DoubleClickListener {
                override fun onSingleClickEvent(view: View?) {
                    // DO STUFF SINGLE CLICK
                    var newLocation: String = listItemView.relocateDst.text.toString()
                    if (newLocation.isEmpty()) {
                        newLocation = "UNTITLED"
                    }
                    println(newLocation)
                    //listItemView.folderButton.text = newAlbumName
                    listItemView.relocate.visibility = View.VISIBLE
                    listItemView.relocateDst.visibility = View.INVISIBLE
                    if (photoId != null) {
                        relocatePhoto(username,albumname,foldername,photoId, newLocation)
                    }
                }

                override fun onDoubleClickEvent(view: View?) {
                    // DO STUFF DOUBLE CLICK
                    listItemView.relocateDst.visibility = View.VISIBLE
                    listItemView.relocate.visibility = View.INVISIBLE
                }
            }))



        }

        return listItemView.root
    }

    private fun mLoad(url:URL): Bitmap? {
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }
}

