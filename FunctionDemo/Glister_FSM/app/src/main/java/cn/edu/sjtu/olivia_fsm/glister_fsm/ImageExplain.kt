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
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_imgexplain.*
import kotlinx.android.synthetic.main.activity_imganalysis.*
import kotlin.math.max
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import androidx.databinding.DataBindingUtil.setContentView


class ImageExplain : AppCompatActivity() {
//    private val scrollView = findViewById<LinearLayout>(R.id.img_explainScrollViewLinear)
    private val img_path: Int = (getIntent().getExtras()?.getInt("img_path"))?:R.drawable.test_analysis_0
    private val saliencyMap_path: Int = (getIntent().getExtras()?.getInt("saliencyMap_path"))?:R.drawable.test_analysis_1
    private val dom_pattern_path: Int = (getIntent().getExtras()?.getInt("dom_pattern_path"))?:R.drawable.ptn8
    private val max_pattern_score: Float =
        (getIntent().getExtras()?.getFloat("max_pattern_score"))?:0.44.toFloat()
    private val overall_score: Float =
        (getIntent().getExtras()?.getFloat("overall_score"))?:4.02.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imgexplain)
        showExplain(img_path, saliencyMap_path, dom_pattern_path, max_pattern_score, overall_score)
    }

    fun showExplain(img_path:Int,
                   saliencyMap_path:Int,
                   dom_pattern_path:Int,
                   max_pattern_score:Float,
                   overall_score:Float){
        // Original Image View
        origImage.setImageResource(img_path)

        // SaliencyMap Image View
        saliencyMap.setImageResource(saliencyMap_path)

        // Pattern Image View
        patternImage.setImageResource(dom_pattern_path)

        // Score Explain Text
        val scoreExpainText = "patten score: $max_pattern_score/1.00 Overall: $overall_score/5.00"
        text_tv_allScore.text = scoreExpainText


//        // Original Image View
//        val orig_imgView = ImageView(this)
//        orig_imgView.setImageResource(img_path) // img_path: R.drawable.(xxx)
//        scrollView.addView(orig_imgView)
//
//        val params_origImgView =
//            LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT)
//        params_origImgView.setMargins(0, 22, 0, 20)
//        orig_imgView.layoutParams = params_origImgView
//
//
//        // SaliencyMap Image View
//        val saliencyMap_imgView = ImageView(this)
//        saliencyMap_imgView.setImageResource(saliencyMap_path) // img_path: R.drawable.(xxx)
//        scrollView.addView(saliencyMap_imgView)
//
//        val params_saliencyMapView =
//            LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT)
//        params_saliencyMapView.setMargins(0, 0, 0, 20)
//        saliencyMap_imgView.layoutParams = params_saliencyMapView
//
//        // Pattern Image View
//        val Pattern_imgView = ImageView(this)
//        Pattern_imgView.setImageResource(dom_pattern_path) // img_path: R.drawable.(xxx)
//        scrollView.addView(Pattern_imgView)
//
//        val params_patternView =
//            LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT)
//        params_patternView.setMargins(0, 20, 0, 20)
//        Pattern_imgView.layoutParams = params_patternView
//
//        // Explain TextView
//        val scoreTextView = TextView(this)
//        scoreTextView.text = scoreExpainText
//        scoreTextView.setTextColor(Color.DKGRAY)
//        scoreTextView.setTextAppearance(Typeface.BOLD)
//        scoreTextView.setPadding(30, 10, 165, 10)
//        scoreTextView.textSize = com.intuit.ssp.R.dimen._14ssp.toFloat()
//        scrollView.addView(scoreTextView)
//
//        val params_scoreTextView =
//            LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT)
//        params_scoreTextView.setMargins(0, 20, 0, 20)
//        scoreTextView.layoutParams = params_scoreTextView


    }
}