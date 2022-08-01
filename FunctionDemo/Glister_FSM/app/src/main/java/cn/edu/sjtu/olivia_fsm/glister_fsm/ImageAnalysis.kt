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

import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main_navigation.*
import kotlinx.android.synthetic.main.activity_imganalysis.*


class ImageAnalysis : AppCompatActivity() {
    private val img_path: Int = (getIntent().getExtras()?.getInt("img_path"))?:R.drawable.test_analysis_0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        img_analysis_OrigImage.setImageResource(img_path)
        setContentView(R.layout.activity_imganalysis)

        img_tvScore.setOnClickListener{
            Toast.makeText(applicationContext,"${img_ratingBar.rating}", Toast.LENGTH_SHORT).show()
        }

        img_analysisBtn.setOnClickListener {
            val imgExplainIntent = Intent(this, ImageExplain::class.java)
            // TODO: put extra params in Array<Album>
            imgExplainIntent.putExtra("img_path", img_path)
//            imgExplainIntent.putExtra("saliencyMap_path", "saliencyMap_path")
//            imgExplainIntent.putExtra("dom_pattern_path", "dom_pattern_path")
//            imgExplainIntent.putExtra("max_pattern_score", "max_pattern_score")
//            imgExplainIntent.putExtra("overall_score", "overall_score")
            startActivity(imgExplainIntent)
        }
    }
}

// Required Params:
//                   img_path:Int,
//                   saliencyMap_path:Int,
//                   dom_pattern_path:Int,
//                   max_pattern_score:Float,
//                   overall_score:Float