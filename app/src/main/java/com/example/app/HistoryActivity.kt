package com.example.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class HistoryActivity : AppCompatActivity() {
    private lateinit var exerciseRecord: List<ExerciseRecordData>
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<LinearLayout>(R.id.navHealth).setOnClickListener { startActivity(Intent(this,HealthyActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navExercise).setOnClickListener { startActivity(Intent(this,ExerciseActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navSetting).setOnClickListener { startActivity(Intent(this,SettingActivity::class.java)) }

        CoroutineScope(Dispatchers.Main).launch {
            getAllExerciseRecord()
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
                        exerciseRecord = convertToRecordData(getAllExerciseRecordResponse.data)
                        Log.d("exerciseRecord",exerciseRecord.toString())
                        for (record in exerciseRecord) {
                            addHistoryBlock(record)
                        }
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

    fun convertToRecordData(exerciseRecords: List<ExerciseRecord>): List<ExerciseRecordData> {
        return exerciseRecords.map { exerciseRecord ->
            ExerciseRecordData(
                duration = exerciseRecord.duration,
                distance = exerciseRecord.distance / 1000,
                avgSpeed = 3600 * exerciseRecord.distance / 1000 / exerciseRecord.duration.toDouble(),
                caloriesBurned = exerciseRecord.caloriesBurned,
                startTime = exerciseRecord.startTime
            )
        }
    }

    private fun addHistoryBlock(exerciseRecord: ExerciseRecordData) {
        val historyContainer = findViewById<LinearLayout>(R.id.historyContainer);
        val historyBlock =
            LayoutInflater.from(this).inflate(R.layout.history_block, historyContainer, false)

        val historyTime = historyBlock.findViewById<TextView>(R.id.historyTime)
        val historyMile = historyBlock.findViewById<TextView>(R.id.historyMile)
        val historySpeed = historyBlock.findViewById<TextView>(R.id.historySpeed)
        val historyCal = historyBlock.findViewById<TextView>(R.id.historyCal)
        val historyStartTime = historyBlock.findViewById<TextView>(R.id.startTime)

        historyTime.text = "运动时间: ${exerciseRecord.duration / 60} min ${exerciseRecord.duration % 60} s"
        historyMile.text = "${String.format("%.2f", exerciseRecord.distance)} km"
        historySpeed.text = "${String.format("%.2f", exerciseRecord.avgSpeed)} km/h"
        historyCal.text = "${String.format("%.2f", exerciseRecord.caloriesBurned)} kcal"

        val startTimeIso = exerciseRecord.startTime // "2024-09-02T00:44:47.405+08:00"
        val zonedDateTime = ZonedDateTime.parse(startTimeIso)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedStartTime = zonedDateTime.format(formatter)
        historyStartTime.text = "开始时间：$formattedStartTime"

        Log.d("historyStartTime",exerciseRecord.startTime)
        historyContainer.addView(historyBlock)
    }
}