package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import com.example.app.data.HeartBeatData
import com.example.app.data.HeightWeightData
import com.example.app.http.BloodPressureService
import com.example.app.http.HealthService
import com.example.app.http.MyToken
import com.example.app.model.CreateBloodPressureRequest
import com.example.app.model.CreateBloodPressureResponse
import com.example.app.model.GetHealthStatusListRequest
import com.example.app.model.GetHealthStatusListResponse
import com.example.app.model.GetHealthStatusResponse
import com.example.app.model.HealthStatusData
import com.example.app.model.PutHealthStatusRequest
import com.example.app.model.PutHealthStatusResponse
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
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.pow
import kotlin.math.round

class HealthConditionActivity : AppCompatActivity() {
    private lateinit var heightWeightData : List<HeightWeightData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_health_condition)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.healthcondition)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCalBMI = findViewById<Button>(R.id.measure)
        val tvBMI = findViewById<TextView>(R.id.tvBMI)
        btnCalBMI.setOnClickListener{
            val height = findViewById<TextView>(R.id.heightView).text.toString().split("：")[1].toFloat()
            val weight = findViewById<TextView>(R.id.weightView).text.toString().split("：")[1].toFloat()
            val BMI: Double = 10000 * weight / height.toDouble().pow(2.0)

            val (tzzk, color) = when {
                BMI < 18.4 -> "偏瘦" to Color.BLUE // 偏瘦 -> 蓝色
                BMI in 18.5..23.9 -> "正常" to Color.parseColor("#4CAF50") // 正常 -> 绿色
                BMI in 24.0..27.9 -> "过重" to Color.YELLOW // 过重 -> 黄色
                BMI in 28.0..32.0 -> "肥胖" to Color.RED // 肥胖 -> 红色
                else -> "重度肥胖" to Color.MAGENTA // 重度肥胖 -> 品红色
            }

            val bmiFormatted = String.format("%.2f", BMI)
            val text = "您的BMI指数为：$bmiFormatted,体质状态：$tzzk"
            val spannableString = SpannableString(text)

            val bmiStartIndex = text.indexOf(bmiFormatted)
            val bmiEndIndex = bmiStartIndex + bmiFormatted.length
            if (bmiStartIndex != -1) {
                spannableString.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    bmiStartIndex,
                    bmiEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            val tzzkStartIndex = text.indexOf("$tzzk")
            val tzzkEndIndex = tzzkStartIndex + "$tzzk".length
            if (tzzkStartIndex != -1) {
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    tzzkStartIndex,
                    tzzkEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    tzzkStartIndex,
                    tzzkEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            tvBMI.text = spannableString
        }

        findViewById<LinearLayout>(R.id.navHealth).setOnClickListener { startActivity(Intent(this,HealthyActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navExercise).setOnClickListener { startActivity(Intent(this,ExerciseActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navSetting).setOnClickListener { startActivity(Intent(this,SettingActivity::class.java)) }

        findViewById<Button>(R.id.input).setOnClickListener {
            showInputDialog()
        }

        CoroutineScope(Dispatchers.Main).launch {
            getHealthStatus()
        }

//        CoroutineScope(Dispatchers.Main).launch {
//            getHealthStatusList()
//        }


        // 添加退出按钮的点击事件监听器
        findViewById<ImageView>(R.id.exit).setOnClickListener {
            finish() // 关闭当前Activity，返回上一个Activity
        }

    }

    private fun showInputDialog() {
        val heightEditText = EditText(this)
        heightEditText.hint = "请输入身高（厘米）"
        val weightEditText = EditText(this)
        weightEditText.hint = "请输入体重（公斤）"

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(heightEditText)
            addView(weightEditText)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("输入身高体重")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                try {
                    val height = heightEditText.text.toString().toFloat()
                    val weight = weightEditText.text.toString().toFloat()
                    if (height != 0F && weight != 0F) {
                        CoroutineScope(Dispatchers.Main).launch {
                            putHealthStatus(height, weight)
                        }
                    }
                    findViewById<TextView>(R.id.heightView).setText("身高（cm）：" + height)
                    findViewById<TextView>(R.id.weightView).setText("体重（kg）：" + weight)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private suspend fun putHealthStatus(height:Float, weight:Float){
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(HealthService::class.java)

        val currentTimeMillis = System.currentTimeMillis()
        val id = (currentTimeMillis / 1000).toInt()
        val putHealthStatusRequest = PutHealthStatusRequest(id,0F,0F,Instant.now().toString(),Instant.now().toString(),0F,0,height,0,Instant.now().toString(),weight)
        Log.i("putHealthStatus", putHealthStatusRequest.toString())

        val call: Call<PutHealthStatusResponse> = service.putHealthStatus(putHealthStatusRequest)
        call.enqueue(object : Callback<PutHealthStatusResponse> {
            override fun onResponse(call: Call<PutHealthStatusResponse>, response: Response<PutHealthStatusResponse>) {
                if (response.isSuccessful()) {
                    val putHealthStatusResponse: PutHealthStatusResponse? = response.body()
                    // 处理响应数据
                    if (putHealthStatusResponse != null) {
                        Log.d("putHealthStatus", "Response: " + putHealthStatusResponse.toString())
                        CoroutineScope(Dispatchers.Main).launch {

                        }
                    }
                } else {
                    Log.e("putHealthStatus", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<PutHealthStatusResponse>, t: Throwable?) {
                Log.e("putHealthStatus", "网络请求失败: ", t)
            }
        })
    }

    private suspend fun getHealthStatus(){
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(HealthService::class.java)

        val call: Call<GetHealthStatusResponse> = service.getHealthStatus()
        call.enqueue(object : Callback<GetHealthStatusResponse> {
            override fun onResponse(call: Call<GetHealthStatusResponse>, response: Response<GetHealthStatusResponse>) {
                if (response.isSuccessful()) {
                    val getHealthStatusResponse: GetHealthStatusResponse? = response.body()
                    // 处理响应数据
                    if (getHealthStatusResponse != null && getHealthStatusResponse.code==1) {
                        Log.d("getHealthStatus", "Response: " + getHealthStatusResponse.toString())
                        findViewById<TextView>(R.id.heightView).setText("身高（cm）：" + getHealthStatusResponse.data.height)
                        findViewById<TextView>(R.id.weightView).setText("体重（kg）：" + getHealthStatusResponse.data.weight)
//                        heightWeightData = convertToHeightWeightData(getHealthStatusResponse.data)
//                        drawChart()
                    }
                } else {
                    Log.e("getHealthStatus", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<GetHealthStatusResponse>, t: Throwable?) {
                Log.e("getHealthStatus", "网络请求失败: ", t)
            }
        })
    }

    private suspend fun getHealthStatusList(){
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(HealthService::class.java)
        val getHealthStatusListRequest = GetHealthStatusListRequest(true, null, null, null, 1, 100, null)
        val call: Call<GetHealthStatusListResponse> = service.getHealthStatusList(getHealthStatusListRequest)
        call.enqueue(object : Callback<GetHealthStatusListResponse> {
            override fun onResponse(call: Call<GetHealthStatusListResponse>, response: Response<GetHealthStatusListResponse>) {
                if (response.isSuccessful()) {
                    val getHealthStatusListResponse: GetHealthStatusListResponse? = response.body()
                    // 处理响应数据
                    if (getHealthStatusListResponse != null) {
                        Log.d("getHealthStatusList", "Response: " + getHealthStatusListResponse.toString())
//                        heightWeightData = convertToHeightWeightData(getHealthStatusResponse.data)
//                        drawChart()
                    }
                } else {
                    Log.e("getHealthStatusList", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<GetHealthStatusListResponse>, t: Throwable?) {
                Log.e("getHealthStatusList", "网络请求失败: ", t)
            }
        })
    }

    fun convertToHeightWeightData(healthStatusData: HealthStatusData): HeightWeightData {
        val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

        val timestamp: Instant = try {
            Instant.parse(healthStatusData.createdAt) // 将 createdAt 转换为 Instant
        } catch (e: DateTimeParseException) {
            Instant.now() // 如果转换失败，则使用当前时间
        }

        return HeightWeightData(
            height = healthStatusData.height,
            weight = healthStatusData.weight,
            timestamp = timestamp,
            duration = Duration.ZERO // 默认值
        )
    }

    private fun getHeightWeightData() :  List<HeightWeightData> {
        val now : Instant = Instant.now()
        return arrayListOf(
            HeightWeightData(170F, 70F, now),
            HeightWeightData(174F, 71F,now.minusSeconds(3600)),
            HeightWeightData(181F,69F, now.minusSeconds(7200)),
            HeightWeightData(183F,73F, now.minusSeconds(3600*3)),
            HeightWeightData(185F,70F, now.minusSeconds(3600*5))
        )
    }

    private fun listToEntry(data: List<HeightWeightData>) : Pair<List<Entry>, List<Entry>> {
        val minTime: Instant = data[0].timestamp
        data.forEach { bp ->
            bp.duration = Duration.between(bp.timestamp, minTime)
        }
        val heightEntries: List<Entry> = data.mapIndexed { idx, bp ->
            Entry(bp.duration.toHours().toFloat(), bp.height.toFloat())
        }
        val weightEntries: List<Entry> = data.mapIndexed { idx, bp ->
            Entry(bp.duration.toHours().toFloat(), bp.weight.toFloat())
        }
        return Pair(heightEntries, weightEntries)
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

        val bpData: List<HeightWeightData> = getHeightWeightData()
        val (heightEntries, weightEntries) = listToEntry(bpData)

        // Height Data Set
        val heightDataSet = LineDataSet(heightEntries, "身高")
        heightDataSet.axisDependency = YAxis.AxisDependency.LEFT
        heightDataSet.color = Color.BLUE // 设置身高线条颜色
        heightDataSet.valueTextColor = Color.BLUE // 设置身高数据点颜色
        heightDataSet.lineWidth = 2f // 设置身高线条宽度
        heightDataSet.setDrawCircles(true) // 显示数据点圆圈
        heightDataSet.setCircleColor(Color.BLUE) // 设置数据点圆圈颜色
        heightDataSet.circleRadius = 4f // 设置数据点圆圈半径
        heightDataSet.setDrawFilled(true) // 填充线下区域
        heightDataSet.fillColor = Color.BLUE // 填充颜色

        // Weight Data Set
        val weightDataSet = LineDataSet(weightEntries, "体重")
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

        adjustYAxisRange(chart, heightEntries, weightEntries)
    }
}