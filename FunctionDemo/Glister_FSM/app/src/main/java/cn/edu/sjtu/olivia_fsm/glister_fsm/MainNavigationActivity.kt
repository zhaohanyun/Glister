package cn.edu.sjtu.olivia_fsm.glister_fsm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import kotlinx.android.synthetic.main.activity_imganalysis.*
import kotlinx.android.synthetic.main.activity_main_navigation.*

class MainNavigationActivity : AppCompatActivity() {
    // Properties
    //private val startRecordIntent = Intent(this, VideoRecordingActivity::class.java)
    //private val imgAnalysisIntent = Intent(this, ImageAnalysis::class.java)
//    fun startRecordIntent(view: View?) = startActivity(Intent(this, VideoRecordingActivity::class.java))


    // Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_navigation)

        val btn_prevEvent0 = findViewById<Button>(R.id.btn_prevEvent0)
        val edt_prevEvent0 = findViewById<EditText>(R.id.edt_prevEvent0)

        val btn_prevEvent0_doubleClick = DoubleClick(object : DoubleClickListener {
            override fun onSingleClickEvent(view: View?) {
                // DO STUFF SINGLE CLICK
            }

            override fun onDoubleClickEvent(view: View?) {
                // DO STUFF DOUBLE CLICK
                btn_prevEvent0.visibility = View.INVISIBLE
                edt_prevEvent0.visibility = View.VISIBLE
            }
        })
        val edt_prevEvent0_doubleClick = DoubleClick(object : DoubleClickListener {
            override fun onSingleClickEvent(view: View?) {
                // DO STUFF SINGLE CLICK
                var newAlbumName: String = edt_prevEvent0.text.toString()
                if (newAlbumName.isEmpty()){
                    newAlbumName = "UNTITLED"
                }
                btn_prevEvent0.text = newAlbumName
                btn_prevEvent0.visibility = View.VISIBLE
                edt_prevEvent0.visibility = View.INVISIBLE
            }

            override fun onDoubleClickEvent(view: View?) {
                // DO STUFF DOUBLE CLICK
                btn_prevEvent0.text = "UNTITLED"
                btn_prevEvent0.visibility = View.INVISIBLE
                edt_prevEvent0.visibility = View.VISIBLE
            }
        })

        btn_prevEvent0.setOnClickListener(btn_prevEvent0_doubleClick)
        edt_prevEvent0.setOnClickListener(edt_prevEvent0_doubleClick)
        btn_imageAnalysis.setOnClickListener {
            // TODO: put extra params in Array<Album>
            // 1. Get img_path from Uri
            // 2. input to image_analysis activity
            // imgAnalysisIntent.putExtra("img_path", img_path)
            val imgAnalysisIntent = Intent(this, ImageAnalysis::class.java)
            startActivity(imgAnalysisIntent)
        }
        btn_naviNewEvent.setOnClickListener{
            val startRecordIntent = Intent(this, VideoRecordingActivity::class.java)
            startActivity(startRecordIntent)
        }

    }


}