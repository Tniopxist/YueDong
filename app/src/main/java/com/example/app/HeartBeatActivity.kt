package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.data.HeartBeatData
import com.example.app.http.HeartRateService
import com.example.app.http.MyToken
import com.example.app.http.RecordService
import com.example.app.model.CreateHeartRateRequest
import com.example.app.model.CreateHeartRateResponse
import com.example.app.model.GetAllHeartRateResponse
import com.example.app.model.HeartRate
import com.example.app.util.DateUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class HeartBeatActivity : AppCompatActivity() {
    private var heartRate: Int = 0 // 心率
    private lateinit var heartBeatData: List<HeartBeatData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_heart_beat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.heartbeat)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        findViewById<LinearLayout>(R.id.navHealth).setOnClickListener { startActivity(Intent(this,HealthyActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navExercise).setOnClickListener { startActivity(Intent(this,ExerciseActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navSetting).setOnClickListener { startActivity(Intent(this,SettingActivity::class.java)) }


        findViewById<Button>(R.id.input).setOnClickListener {
            showInputDialog()
        }

        CoroutineScope(Dispatchers.Main).launch {
            getAllHeartRate()
        }
    }

    private fun getHeartBeatData() :  List<HeartBeatData> {
//        val now : Instant = Instant.now()
//        return arrayListOf(
//            HeartBeatData(80, now),
//            HeartBeatData(87, now.minusSeconds(3600)),
//            HeartBeatData(78, now.minusSeconds(7200)),
//            HeartBeatData(110, now.minusSeconds(3600*3)),
//            HeartBeatData(130, now.minusSeconds(3600*5))
//        )
        Log.i("getHeartBeatData",heartBeatData.toString())
        return heartBeatData
    }

    private fun listToEntry(data: List<HeartBeatData>) : List<Entry>{
        val minTime : Instant = data[0].timestamp
        data.forEach { bp ->
            bp.duration = Duration.between(minTime,bp.timestamp)
        }
        val entries: List<Entry> = data.mapIndexed { idx, bp ->
            Entry(bp.duration.toHours().toFloat(), bp.hb.toFloat()) // duration 1h
        }
        Log.i("listToEntry",entries.toString())
        return entries
    }

    private fun drawChart() {
        val chart: LineChart = findViewById(R.id.chart)

        val bpData: List<HeartBeatData> = getHeartBeatData()
        val entries: List<Entry> = listToEntry(bpData)

        // Heart Rate Data Set
        val lineDataSet = LineDataSet(entries, "心率")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.color = Color.BLUE // 设置心率线条颜色
        lineDataSet.valueTextColor = Color.BLUE // 设置心率数据点颜色
        lineDataSet.lineWidth = 2f // 设置心率线条宽度
        lineDataSet.setDrawCircles(true) // 显示数据点圆圈
        lineDataSet.setCircleColor(Color.BLUE) // 设置数据点圆圈颜色
        lineDataSet.circleRadius = 4f // 设置数据点圆圈半径
//        lineDataSet.setDrawFilled(true) // 填充线下区域
//        lineDataSet.fillColor = Color.BLUE // 填充颜色

        // Create LineData with DataSet
        val lineData = LineData(lineDataSet)
        chart.data = lineData

        // Customize chart
        chart.setDrawBorders(true)
//        chart.setBorderColor(Color.LTGRAY) // 设置边框颜色
//        chart.setBorderWidth(1f) // 设置边框宽度
//        chart.setBackgroundColor(Color.WHITE) // 设置背景颜色

        val xAxis: XAxis = chart.xAxis
        val yAxis: YAxis = chart.axisLeft
        val yAxisRight: YAxis = chart.axisRight

        // Customize X Axis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setGranularity(1.0F) // 1h
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val T: Int = 1 // 1h
                val minTime: Instant = bpData[0].timestamp
                val idx: Long = (value / T).toLong()
                val t: Instant = minTime.plusSeconds(idx * 3600) // 1h
                return DateUtils.formatInstantToDateString(t)
            }
        }

        // Customize Y Axis
        yAxis.setDrawGridLines(false)
        yAxis.setDrawLabels(true)
        yAxis.axisMinimum = entries.minOfOrNull { it.y }?.minus(10) ?: 0f // Adjust as needed
        yAxis.axisMaximum = entries.maxOfOrNull { it.y }?.plus(10) ?: 200f // Adjust as needed

        // Disable Right Y Axis
        yAxisRight.isEnabled = false

        // Enable and Customize Legend
        chart.legend.isEnabled = true
        chart.legend.textColor = Color.BLACK // Legend text color
        chart.legend.textSize = 12f // Legend text size

        // Refresh chart
        chart.invalidate()
    }

    private fun showInputDialog() {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("输入心率")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                heartRate = editText.text.toString().toInt()
                if (heartRate != 0){
                    CoroutineScope(Dispatchers.Main).launch {
                        createHeartRate()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private suspend fun createHeartRate(){
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(HeartRateService::class.java)

        val currentTimeMillis = System.currentTimeMillis()
        val id = (currentTimeMillis / 1000).toInt()

        val createHeartRateRequest = CreateHeartRateRequest(id,Instant.now().toString(), heartRate, Instant.now().toString())

        Log.i("createHeartRateRequest", createHeartRateRequest.toString())
        val call: Call<CreateHeartRateResponse> = service.createHeartRate(createHeartRateRequest)
        call.enqueue(object : Callback<CreateHeartRateResponse> {
            override fun onResponse(call: Call<CreateHeartRateResponse>, response: Response<CreateHeartRateResponse>) {
                if (response.isSuccessful()) {
                    val createHeartRateResponse: CreateHeartRateResponse? = response.body()
                    // 处理响应数据
                    if (createHeartRateResponse != null) {
                        Log.d("createHeartRate", "Response: " + createHeartRateResponse.toString())
                        CoroutineScope(Dispatchers.Main).launch {
                            getAllHeartRate()
                        }
                    }
                } else {
                    Log.e("createHeartRate", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<CreateHeartRateResponse>, t: Throwable?) {
                Log.e("createHeartRate", "网络请求失败: ", t)
            }
        })
    }

    private suspend fun getAllHeartRate() {
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(HeartRateService::class.java)

        val call: Call<GetAllHeartRateResponse> = service.getAllHeartRate()
        call.enqueue(object : Callback<GetAllHeartRateResponse> {
            override fun onResponse(call: Call<GetAllHeartRateResponse>, response: Response<GetAllHeartRateResponse>) {
                if (response.isSuccessful()) {
                    val getAllHeartRateResponse: GetAllHeartRateResponse? = response.body()
                    // 处理响应数据
                    if (getAllHeartRateResponse != null && getAllHeartRateResponse.data.size>0) {
                        Log.d("getAllHeartRate", "Response: " + getAllHeartRateResponse.toString())

                        heartBeatData = convertToHeartBeatData(getAllHeartRateResponse.data)
                        drawChart()
                    }
                } else {
                    Log.e("getAllHeartRate", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<GetAllHeartRateResponse>, t: Throwable?) {
                Log.e("getAllHeartRate", "网络请求失败: ", t)
            }
        })
    }

    fun convertToHeartBeatData(heartRates: List<HeartRate>): List<HeartBeatData> {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return heartRates.map { heartRate ->
            val createdAtInstant = OffsetDateTime.parse(heartRate.createdAt, formatter).toInstant()

            HeartBeatData(
                hb = heartRate.tps,
                timestamp = createdAtInstant,
                duration = Duration.ZERO
            )
        }
    }
}