package cn.edu.sjtu.glister.glisterfrontend

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast

fun Context.toast(message: String, short: Boolean = true) {
    Toast.makeText(this, message, if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
}
fun ImageView.display(uri: Uri) {
    setImageURI(uri)
    visibility = View.VISIBLE
}