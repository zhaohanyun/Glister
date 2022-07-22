package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.net.URLDecoder


fun Context.toast(message: String, short: Boolean = true) {
    Toast.makeText(this, message, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
}

fun ImageView.display(uri: Uri) {
    setImageURI(uri)
    visibility = View.VISIBLE
}

fun Uri.toFile(context: Context,uri:Uri): File? {

//    if (!(authority == "media" || authority == "com.google.android.apps.photos.contentprovider")) {
//        // for on-device media files only
//        context.toast("Media file not on device")
//        Log.d("Uri.toFile", authority.toString())
//        return null
//    }

    if (scheme.equals("content")) {
        var cursor: Cursor? = null
        try {
            //lab version
            cursor = context.getContentResolver().query(
                this, arrayOf("_data"),
                null, null, null
            )

            cursor?.run {
                moveToFirst()
                val temp=getColumnIndexOrThrow("_data")
                val sth=getString(temp)
                return File(getString(getColumnIndexOrThrow("_data")))
            }

            //my own method to parse uri. But require starting activity through Action_Read_Document
//            val newuri:String = URLDecoder.decode(this.toString(), "UTF-8")
//            cursor = context.getContentResolver().query(
//                Uri.parse(newuri), arrayOf("_display_name", "relative_path"),
//                null, null, null
//            )
//
//            cursor?.run {
//                moveToFirst()
//                val filename=getString(getColumnIndexOrThrow("_display_name"))
//                val relativePath=getString(getColumnIndexOrThrow("relative_path"))
//                val absolutePath:String="/storage/emulated/0/"
//                val fullPath:String=absolutePath+relativePath+filename
//                return File(fullPath)
//            }


        } finally {
            cursor?.close()
        }
    }
    return null
}

