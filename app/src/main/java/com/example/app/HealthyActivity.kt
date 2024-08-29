package com.example.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.grabner.circleprogress.CircleProgressView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.MapsInitializer
import com.example.app.data.BloodPressureData

class HealthyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_healthy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.healthy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val mCircleView1 = findViewById<CircleProgressView>(R.id.circleView1)
        val mCircleView2 = findViewById<CircleProgressView>(R.id.circleView2)
        val mCircleView3 = findViewById<CircleProgressView>(R.id.circleView3)
        initCircleView(mCircleView1)
        initCircleView(mCircleView2)
        initCircleView(mCircleView3)

        mCircleView1.setValue(getStepProgress().toFloat())
        mCircleView2.setValue(getMileProgress().toFloat())
        mCircleView3.setValue(getTimeProgress().toFloat())


        val ydls = findViewById<LinearLayout>(R.id.ydls)
        ydls.setOnClickListener{
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
        val stsz = findViewById<LinearLayout>(R.id.stsz)
        stsz.setOnClickListener{
            val intent = Intent(this, HealthConditionActivity::class.java)
            startActivity(intent)
        }
        val xyjc = findViewById<LinearLayout>(R.id.xyjc)
        xyjc.setOnClickListener{
            val intent = Intent(this, BloodPressureActivity::class.java)
            startActivity(intent)
        }
        val xljc = findViewById<LinearLayout>(R.id.xljc)
        xljc.setOnClickListener{
            val intent = Intent(this, HeartBeatActivity::class.java)
            startActivity(intent)
        }

        val toDetailPlan = findViewById<ImageView>(R.id.toDetailPlan)
        toDetailPlan.setOnClickListener {
            MaterialDialog(this, BottomSheet()).show {
                customView(R.layout.dialog_input) // 指定布局文件
                positiveButton(text = "确定") {
//                    // 处理输入内容
//                    val inputField1 = it.findViewById<EditText>(R.id.inputField1).text.toString()
//                    val inputField2 = it.findViewById<EditText>(R.id.inputField2).text.toString()
//                    val inputField3 = it.findViewById<EditText>(R.id.inputField3).text.toString()
                }
                negativeButton(text = "取消")
            }
        }
    }

    fun getStepProgress() : Int{
        val targetStep = findViewById<TextView>(R.id.targetStepCount)
        var step : Float = 2000F
        targetStep.setText("目标步数：$step 步")
        var curStep : Float = 1000F
        findViewById<TextView>(R.id.SetStepCount).text = "$curStep 步"
        return ((curStep / step) * 100).toInt()
    }

    fun getMileProgress() : Int{
        val targetMile = findViewById<TextView>(R.id.targetMileCount)
        var mile : Float = 3F
        targetMile.setText("目标里程：$mile 千米")
        var curMile : Float = 1F
        findViewById<TextView>(R.id.SetMileCount).text = "$curMile 千米"
        return ((curMile / mile) * 100).toInt()
    }

    fun getTimeProgress() : Int{
        val targetTime = findViewById<TextView>(R.id.targetTimeCount)
        var time : Float = 20F
        targetTime.setText("目标时间：$time 分钟")
        var curTime : Float = 5F
        findViewById<TextView>(R.id.SetTimeCount).text = "$curTime 分钟"
        return ((curTime / time) * 100).toInt()
    }

    fun initCircleView(mCircleView:CircleProgressView) {

    }
}