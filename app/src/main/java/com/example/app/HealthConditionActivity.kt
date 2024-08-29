package com.example.app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.data.HeartBeatData
import com.example.app.data.HeightWeightData
import com.example.app.util.DateUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.Duration
import java.time.Instant
import kotlin.math.pow
import kotlin.math.round

class HealthConditionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_health_condition)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.healthcondition)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawChart()
        val btnCalBMI = findViewById<Button>(R.id.measure)
        val tvBMI = findViewById<TextView>(R.id.tvBMI)
        btnCalBMI.setOnClickListener{
            val height = getHeightWeightData().last().height
            val weight = getHeightWeightData().last().weight
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