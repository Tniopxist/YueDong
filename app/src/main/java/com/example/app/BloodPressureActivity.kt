package com.example.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.data.BloodPressureData
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


class BloodPressureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_blood_pressure)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bloodpressure)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawChart()
    }

    private fun getBloodPressureData() :  List<BloodPressureData> {
        val now : Instant = Instant.now()
        return arrayListOf(
            BloodPressureData(110, now),
            BloodPressureData(120, now.minusSeconds(3600)),
            BloodPressureData(130, now.minusSeconds(7200)),
            BloodPressureData(160, now.minusSeconds(3600*3)),
            BloodPressureData(130, now.minusSeconds(3600*5))
        )
    }

    private fun listToEntry(data: List<BloodPressureData>) : List<Entry>{
        val minTime : Instant = data[0].timestamp
        data.forEach { bp ->
            bp.duration = Duration.between(bp.timestamp, minTime)
        }
        val entries: List<Entry> = data.mapIndexed { idx, bp ->
            Entry(bp.duration.toHours().toFloat(), bp.bp.toFloat()) // duration 1h
        }
        return entries
    }

    private fun drawChart() {
        val chart: LineChart = findViewById(R.id.chart)

        val bpData: List<BloodPressureData> = getBloodPressureData()
        val entries: List<Entry> = listToEntry(bpData)

        // Blood Pressure Data Set
        val lineDataSet = LineDataSet(entries, "血压")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.color = Color.BLUE // 设置血压线条颜色
        lineDataSet.valueTextColor = Color.BLUE // 设置血压数据点颜色
        lineDataSet.lineWidth = 2f // 设置血压线条宽度
        lineDataSet.setDrawCircles(true) // 显示数据点圆圈
        lineDataSet.setCircleColor(Color.BLUE) // 设置数据点圆圈颜色
        lineDataSet.circleRadius = 4f // 设置数据点圆圈半径
//        lineDataSet.setDrawFilled(true) // 填充线下区域
//        lineDataSet.fillColor = Color.GREEN // 填充颜色

        // Create LineData with DataSet
        val lineData = LineData(lineDataSet)
        chart.data = lineData

        // Customize chart
        chart.setDrawBorders(true)

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
        yAxis.axisMaximum = entries.maxOfOrNull { it.y }?.plus(10) ?: 100f // Adjust as needed

        // Disable Right Y Axis
        yAxisRight.isEnabled = false

        // Enable and Customize Legend
        chart.legend.isEnabled = true
        chart.legend.textColor = Color.BLACK // Legend text color
        chart.legend.textSize = 12f // Legend text size

        // Refresh chart
        chart.invalidate()
    }

}

