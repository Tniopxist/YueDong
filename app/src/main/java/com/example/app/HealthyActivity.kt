package com.example.app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import com.example.app.data.ExerciseRecordData
import com.example.app.http.MyToken
import com.example.app.http.RecordService
import com.example.app.model.ExerciseRecord
import com.example.app.model.GetAllExerciseRecordResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.random.Random

class HealthyActivity : AppCompatActivity() {
    private var curStep : Int = 0
    private var curMile : Float = 0F
    private var curTime : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_healthy)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.healthy)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                    val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    // 处理输入内容
                    val targetStep = it.findViewById<EditText>(R.id.inputField1).text.toString()
                    val targetMile = it.findViewById<EditText>(R.id.inputField2).text.toString()
                    val targetTime = it.findViewById<EditText>(R.id.inputField3).text.toString()
                    editor.putString("targetStep", targetStep)
                    editor.putString("targetMile", targetMile)
                    editor.putString("targetTime", targetTime)
                    editor.apply()

                    Log.i("targetStep",targetStep)
                    Log.i("targetMile", targetMile)
                    Log.i("targetTime", targetTime)

                    initCircleView()
                }
                negativeButton(text = "取消")
            }
        }

        findViewById<LinearLayout>(R.id.navHealth).setOnClickListener { startActivity(Intent(this,HealthyActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navExercise).setOnClickListener { startActivity(Intent(this,ExerciseActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navSetting).setOnClickListener { startActivity(Intent(this,SettingActivity::class.java)) }

        CoroutineScope(Dispatchers.Main).launch {
            getAllExerciseRecord()
        }
    }

    private fun initCircleView() {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val targetStep = sharedPreferences.getString("targetStep", "0")
        val targetMile = sharedPreferences.getString("targetMile", "0")
        val targetTime = sharedPreferences.getString("targetTime", "0")
        findViewById<TextView>(R.id.targetStepCount).text = "目标步数：$targetStep 步"
        findViewById<TextView>(R.id.targetMileCount).text = "目标里程：$targetMile 千米"
        findViewById<TextView>(R.id.targetTimeCount).text = "目标时间：$targetTime 分钟"


        findViewById<TextView>(R.id.SetStepCount).text = "$curStep 步"
        findViewById<TextView>(R.id.SetMileCount).text = "${String.format("%.2f",curMile)} 千米"
        findViewById<TextView>(R.id.SetTimeCount).text = "$curTime 分钟"

        val mCircleView1 = findViewById<CircleProgressView>(R.id.circleView1)
        val mCircleView2 = findViewById<CircleProgressView>(R.id.circleView2)
        val mCircleView3 = findViewById<CircleProgressView>(R.id.circleView3)
        if (targetStep != null) {
            Log.i("mCircleView1",(curStep.toFloat() / targetStep.toFloat()).toString())
        }
        if (targetStep != null) {
            if (targetMile != null) {
                if (targetTime != null) {
                    if (!targetStep.toString().equals("0") && !targetMile.toString().equals("0") && !targetTime.toString().equals("0")) {
                        Log.i("mCircleView1",(curStep.toFloat() / targetStep.toFloat()).toString())
                        mCircleView1.setValue(100*curStep.toFloat() / targetStep.toFloat())
                        mCircleView2.setValue(100*curMile.toFloat() / targetMile.toFloat())
                        mCircleView3.setValue(100*curTime.toFloat() / targetTime.toFloat())
                    }
                }
            }
        }
    }

    private fun getAllExerciseRecord(){
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(RecordService::class.java)

        val call: Call<GetAllExerciseRecordResponse> = service.getAllExerciseRecord()
        call.enqueue(object : Callback<GetAllExerciseRecordResponse> {
            override fun onResponse(call: Call<GetAllExerciseRecordResponse>, response: Response<GetAllExerciseRecordResponse>) {
                if (response.isSuccessful()) {
                    val getAllExerciseRecordResponse: GetAllExerciseRecordResponse? = response.body()
                    // 处理响应数据
                    if (getAllExerciseRecordResponse != null) {
                        Log.d("getAllExerciseRecordResponse", "Response: " + getAllExerciseRecordResponse.toString())

                        for (record in getAllExerciseRecordResponse.data) {
                            if (isToday((record.startTime))) {
                                curTime += record.duration/60 //min
                                curMile += record.distance / 1000 //km
                                curStep += generateStepsFromDistance(record.distance)
                            }
                        }
                        Log.i("curTime",curTime.toString())
                        Log.i("curMile",curMile.toString())
                        Log.i("curStep",curStep.toString())
                        Log.i("getAllExerciseRecordResponse.data",getAllExerciseRecordResponse.data.toString())

                        initCircleView()
                    }
                } else {
                    Log.e("getAllExerciseRecordResponse", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<GetAllExerciseRecordResponse>, t: Throwable?) {
                Log.e("getAllExerciseRecordResponse", "网络请求失败: ", t)
            }
        })
    }

    fun isToday(startTime: String): Boolean {
        val today = LocalDate.now()
        return try {
            // 使用 ZonedDateTime 解析时间字符串
            val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
            val startDateTime = ZonedDateTime.parse(startTime, dateTimeFormatter)
            val startDate = startDateTime.toLocalDate()

            Log.d("isToday", "Parsed date: $startDate")
            Log.d("isToday", "Today's date: $today")

            startDate == today
        } catch (e: DateTimeParseException) {
            Log.e("isToday", "Unable to parse startTime: ${e.message}")
            false
        }
    }

    fun generateStepsFromDistance(distanceInMeters: Float): Int {
        val minStepLength = 0.7
        val maxStepLength = 0.8

        val minSteps = (distanceInMeters / maxStepLength).toInt()
        val maxSteps = (distanceInMeters / minStepLength).toInt()

        return Random.nextInt(minSteps, maxSteps + 1)
    }
}