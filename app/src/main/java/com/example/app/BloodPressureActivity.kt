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
import com.example.app.data.BloodPressureData
import com.example.app.data.HeartBeatData
import com.example.app.data.HeightWeightData
import com.example.app.http.BloodPressureService
import com.example.app.http.HeartRateService
import com.example.app.http.MyToken
import com.example.app.model.BloodPressure
import com.example.app.model.CreateBloodPressureRequest
import com.example.app.model.CreateBloodPressureResponse
import com.example.app.model.CreateHeartRateRequest
import com.example.app.model.CreateHeartRateResponse
import com.example.app.model.GetAllBloodPressureOfUserResponse
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
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


class BloodPressureActivity : AppCompatActivity() {
    private lateinit var bloodPressureData : List<BloodPressureData>
    private var systolicPressure : Int = 0
    private var diastolicPressure : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_blood_pressure)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bloodpressure)) { v, insets ->
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
            getAllBloodPressureOfUser()
        }
    }

    private fun showInputDialog() {
        val systolicEditText = EditText(this)
        systolicEditText.hint = "请输入收缩压（高压）"
        val diastolicEditText = EditText(this)
        diastolicEditText.hint = "请输入舒张压（低压）"

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(systolicEditText)
            addView(diastolicEditText)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("输入血压")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                try {
                    systolicPressure = systolicEditText.text.toString().toInt()
                    diastolicPressure = diastolicEditText.text.toString().toInt()
                    if (diastolicPressure != 0 && systolicPressure!=0){
                        CoroutineScope(Dispatchers.Main).launch {
                            createBloodPressure()
                        }
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun getBloodPressureData() :  List<BloodPressureData> {
        return bloodPressureData
    }

    private fun listToEntry(data: List<BloodPressureData>) : Pair<List<Entry>, List<Entry>>{
        val minTime : Instant = data[0].timestamp
        data.forEach { bp ->
            bp.duration = Duration.between(minTime,bp.timestamp)
        }
        val diastolicPressureEntries: List<Entry> = data.mapIndexed { idx, bp ->
            Entry(bp.duration.toHours().toFloat(), bp.diastolicPressure.toFloat()) // duration 1h
        }
        val systolicPressureEntries: List<Entry> = data.mapIndexed { idx, bp ->
            Entry(bp.duration.toHours().toFloat(), bp.systolicPressure.toFloat()) // duration 1h
        }
        return Pair(diastolicPressureEntries,systolicPressureEntries) //低压 高压
    }

    private fun adjustYAxisRange(chart: LineChart, heightEntries: List<Entry>, weightEntries: List<Entry>) {
        val yAxisLeft: YAxis = chart.axisLeft
        val yAxisRight: YAxis = chart.axisRight

        val heightMax = heightEntries.maxOfOrNull { it.y } ?: 0f
        val heightMin = heightEntries.minOfOrNull { it.y } ?: 0f
        val weightMax = weightEntries.maxOfOrNull { it.y } ?: 0f
        val weightMin = weightEntries.minOfOrNull { it.y } ?: 0f

        yAxisLeft.axisMaximum = heightMax + 10f // Adjust according to your needs
        yAxisLeft.axisMinimum = heightMin - 1f

        yAxisRight.axisMaximum = weightMax + 1f // Adjust according to your needs
        yAxisRight.axisMinimum = weightMin - 10f
    }

    private fun drawChart() {
        val chart: LineChart = findViewById(R.id.chart)

        val bpData: List<BloodPressureData> = getBloodPressureData()
        Log.d("bpData",bpData.toString())
        val (diastolicPressureEntries,systolicPressureEntries) = listToEntry(bpData)
        Log.d("diastolicPressureEntries",diastolicPressureEntries.toString())
        Log.d("systolicPressureEntries",systolicPressureEntries.toString())

        // 低压
        val heightDataSet = LineDataSet(diastolicPressureEntries, "低压")
        heightDataSet.axisDependency = YAxis.AxisDependency.LEFT
        heightDataSet.color = Color.BLUE // 设置身高线条颜色
        heightDataSet.valueTextColor = Color.BLUE // 设置身高数据点颜色
        heightDataSet.lineWidth = 2f // 设置身高线条宽度
        heightDataSet.setDrawCircles(true) // 显示数据点圆圈
        heightDataSet.setCircleColor(Color.BLUE) // 设置数据点圆圈颜色
        heightDataSet.circleRadius = 4f // 设置数据点圆圈半径
        heightDataSet.setDrawFilled(true) // 填充线下区域
        heightDataSet.fillColor = Color.BLUE // 填充颜色

        // 高压
        val weightDataSet = LineDataSet(systolicPressureEntries, "高压")
        weightDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        weightDataSet.color = Color.RED // 设置体重线条颜色
        weightDataSet.valueTextColor = Color.RED // 设置体重数据点颜色
        weightDataSet.lineWidth = 2f // 设置体重线条宽度
        weightDataSet.setDrawCircles(true) // 显示数据点圆圈
        weightDataSet.setCircleColor(Color.RED) // 设置数据点圆圈颜色
        weightDataSet.circleRadius = 4f // 设置数据点圆圈半径
        weightDataSet.setDrawFilled(true) // 填充线下区域
        weightDataSet.fillColor = Color.RED // 填充颜色

        // Create LineData with both DataSets
        val lineData = LineData(heightDataSet, weightDataSet)
        chart.data = lineData

        // Customize chart
        chart.setDrawBorders(true)
        val xAxis: XAxis = chart.xAxis
        val yAxisLeft: YAxis = chart.axisLeft
        val yAxisRight: YAxis = chart.axisRight

        xAxis.setDrawGridLines(false)
        yAxisLeft.setDrawGridLines(false)
        yAxisRight.setDrawGridLines(false)
        chart.axisRight.setEnabled(true)

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
        adjustYAxisRange(chart, diastolicPressureEntries, systolicPressureEntries)
        chart.invalidate()
    }

    private suspend fun createBloodPressure(){
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(BloodPressureService::class.java)

        val currentTimeMillis = System.currentTimeMillis()
        val id = (currentTimeMillis / 1000).toInt()

        val createBloodPressureRequest = CreateBloodPressureRequest(id,Instant.now().toString(),diastolicPressure,systolicPressure,Instant.now().toString())

        Log.i("createBloodPressureRequest", createBloodPressureRequest.toString())
        val call: Call<CreateBloodPressureResponse> = service.createBloodPressure(createBloodPressureRequest)
        call.enqueue(object : Callback<CreateBloodPressureResponse> {
            override fun onResponse(call: Call<CreateBloodPressureResponse>, response: Response<CreateBloodPressureResponse>) {
                if (response.isSuccessful()) {
                    val createBloodPressure: CreateBloodPressureResponse? = response.body()
                    // 处理响应数据
                    if (createBloodPressure != null) {
                        Log.d("createBloodPressure", "Response: " + createBloodPressure.toString())
                        CoroutineScope(Dispatchers.Main).launch {
                            getAllBloodPressureOfUser()
                        }
                    }
                } else {
                    Log.e("createBloodPressure", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<CreateBloodPressureResponse>, t: Throwable?) {
                Log.e("createBloodPressure", "网络请求失败: ", t)
            }
        })
    }

    private suspend fun getAllBloodPressureOfUser() {
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(BloodPressureService::class.java)

        val call: Call<GetAllBloodPressureOfUserResponse> = service.getAllBloodPressureOfUser()
        call.enqueue(object : Callback<GetAllBloodPressureOfUserResponse> {
            override fun onResponse(call: Call<GetAllBloodPressureOfUserResponse>, response: Response<GetAllBloodPressureOfUserResponse>) {
                if (response.isSuccessful()) {
                    val getAllBloodPressureOfUserResponse: GetAllBloodPressureOfUserResponse? = response.body()
                    // 处理响应数据
                    if (getAllBloodPressureOfUserResponse != null && getAllBloodPressureOfUserResponse.data.size>0) {
                        Log.d("getAllBloodPressureOfUser", "Response: " + getAllBloodPressureOfUserResponse.toString())

                        bloodPressureData = convertToBloodPressureData(getAllBloodPressureOfUserResponse.data)
                        Log.d("bloodPressureData", bloodPressureData.toString())
                        drawChart()
                    }
                } else {
                    Log.e("getAllBloodPressureOfUser", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<GetAllBloodPressureOfUserResponse>, t: Throwable?) {
                Log.e("getAllBloodPressureOfUser", "网络请求失败: ", t)
            }
        })
    }

    fun convertToBloodPressureData(bloodPressures: List<BloodPressure>): List<BloodPressureData> {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return bloodPressures.map { bloodPressure ->
            val createdAtInstant = OffsetDateTime.parse(bloodPressure.createdAt, formatter).toInstant()

            BloodPressureData(
                systolicPressure = bloodPressure.systolic,
                diastolicPressure = bloodPressure.diastolic,
                timestamp = createdAtInstant,
                duration = Duration.ZERO
            )
        }
    }
}

