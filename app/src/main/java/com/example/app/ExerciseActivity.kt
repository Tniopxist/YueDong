package com.example.app

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.MapsInitializer
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import com.example.app.http.MyToken
import com.example.app.http.RecordService
import com.example.app.model.InsertExerciseRequest
import com.example.app.model.InsertExerciseResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant


class ExerciseActivity : AppCompatActivity() ,AMapLocationListener{
    private var mMapView : MapView? = null
    private var aMap: AMap? = null
    private var locationClient: AMapLocationClient? = null
    private val pathPoints = mutableListOf<LatLng>()
    private var shouldDrawPath = false  // 用于控制是否绘制路径和累加距离
    // 用于存储前一个点和当前点
    private var lastPoint: LatLng? = null
    private var currentPoint: LatLng? = null

    private lateinit var useTime: TextView
    private lateinit var runMile: TextView
    private lateinit var runSpeed: TextView
    private lateinit var heartBeat: TextView
    private lateinit var runEnergy: TextView
    private lateinit var startExercise: ImageView
    private lateinit var endExercise: ImageView
    private lateinit var communityRank: ImageView

    private var isstarting = false  //是否在运动中
    private var isRunning = false   // 是否正在运动

    private var elapsedTime = 0    // 时间
    private var mile = 0F    // 里程
    private var speed = 0F   // 配速
    private var hb = 0      // 心率
    private var energy = 0F  // 耗能
    private var starttime = ""  // 开始时间
    private var endtime = ""   // 结束时间

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_exercise)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.exercise)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 地图
        initMap(savedInstanceState)

        // 运动数据
        recordExercise()

        // 社区排名
        communityRank = findViewById(R.id.communityRank)
        communityRank.setOnClickListener {
            val intent = Intent(this, RankActivity::class.java)
            // 启动新的Activity
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navHealth).setOnClickListener { startActivity(Intent(this,HealthyActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navExercise).setOnClickListener { startActivity(Intent(this,ExerciseActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navSetting).setOnClickListener { startActivity(Intent(this,SettingActivity::class.java)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
        aMap?.clear()
        locationClient?.onDestroy()
        scope.cancel()
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }


    // 初始化地图及定位信息
    private fun initMap(savedInstanceState: Bundle?){
        // 更新隐私合规设置
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)

        // 初始化地图
        mMapView = findViewById(R.id.map)
        mMapView!!.onCreate(savedInstanceState);
        mMapView?.setBackgroundColor(Color.TRANSPARENT)

        // 获取地图对象
        aMap = mMapView!!.getMap();
        aMap?.mapType = AMap.MAP_TYPE_NORMAL  // 使用标准地图模式

        // 地图其他设置 指南标识 比例尺
        val mUiSettings: UiSettings = aMap!!.getUiSettings()
        mUiSettings.setCompassEnabled(true)
        mUiSettings.setScaleControlsEnabled(true)

        // 检查并请求权限
        checkLocationPermission()

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else {
            // 检查是否开启定位、是否有网
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGpsEnabled && !isNetworkEnabled) {
                // 提示用户开启定位服务
                Toast.makeText(this, "请开启定位服务", Toast.LENGTH_SHORT).show()
            }
            // 已授予权限，初始化定位客户端
            aMap!!.isMyLocationEnabled = true
            initLocationClient()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限授予，初始化定位客户端
                initLocationClient()
            } else {
                // 权限被拒绝
                Toast.makeText(this, "需要定位权限来显示您的位置", Toast.LENGTH_SHORT).show()
                // 引导用户到设置页面
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
                checkLocationPermission()
            }
        }
    }

    // 运动模块：计时与数据
    private fun recordExercise(){
        useTime = findViewById(R.id.UseTime)
        startExercise = findViewById(R.id.startExercise)
        endExercise = findViewById(R.id.endExercise)

        // 计时
        runnable = object : Runnable {
            override fun run() {
                elapsedTime++
                updateInfo()
                handler.postDelayed(this, 1000)
            }
        }

        // 开始，暂停运动
        startExercise.setOnClickListener {
            // 还未开始运动
            if(!isstarting){
                isstarting = true
                Log.d("Star_btn:", "start")
                starttime = Instant.now().toString()
                Log.d("Starttime:", starttime)
                val end: LinearLayout = findViewById(R.id.end)
                end.setVisibility(View.VISIBLE)
//                pathPoints.clear()
            }

            if (isRunning) {    // 正在运动
                pauseTimer()

            } else {    //结束运动
                startTimer()
            }
        }

        // 结束运动
        endExercise.setOnClickListener{
            if(isstarting){
                endTimer()
                isstarting = false
                val end: LinearLayout = findViewById(R.id.end)
                end.setVisibility(View.INVISIBLE)
            }
        }

    }

    private fun initLocationClient() {
        // 初始化定位客户端
        locationClient = AMapLocationClient(applicationContext)
        val locationOption = AMapLocationClientOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            interval = 2000 // 定位间隔
            isOnceLocation = false // 持续定位
            isOnceLocationLatest = true // 获取最近的定位结果
            isNeedAddress = false // 需要地址信息
        }
        aMap!!.isMyLocationEnabled = true
        aMap!!.animateCamera(CameraUpdateFactory.zoomTo(19F))     //地图精确度3~19F

        locationClient!!.setLocationOption(locationOption)    // 设置定位参数
        locationClient!!.setLocationListener(this)    // 设置定位监听

    }

    // 开始计时
    private fun startTimer() {
        isRunning = true
        shouldDrawPath = false
        startTracking()
        startExercise = findViewById(R.id.startExercise)
        startExercise.setImageResource(R.drawable.pauseexercise)
        handler.post(runnable)
    }

    // 暂停计时
    private fun pauseTimer() {
        isRunning = false
        pauseTracking()
        startExercise = findViewById(R.id.startExercise)
        startExercise.setImageResource(R.drawable.pause)
        handler.removeCallbacks(runnable)
    }

    // 结束计时
    private fun endTimer(){
        isRunning = false
        pauseTracking()
        startExercise = findViewById(R.id.startExercise)
        startExercise.setImageResource(R.drawable.go)
        handler.removeCallbacks(runnable)

        // 记录数据
        recordEx()
    }

    // 运动记录
    private fun recordEx(){
        endtime = Instant.now().toString()
        Log.d("Endtime:", endtime)

        CoroutineScope(Dispatchers.Main).launch {
            InsertRecords()
            // 重置数据
//            elapsedTime = 0
//            mile = 0F
//            speed = 0F
//            hb = 0
//            energy = 0F
//            starttime = ""
//            endtime = ""
//
//            shouldDrawPath = false
//            lastPoint = null
//            currentPoint = null
//
//            aMap!!.clear()
//            pathPoints.clear()
//            aMap!!.isMyLocationEnabled = true
//            aMap!!.animateCamera(CameraUpdateFactory.zoomTo(19F))
//            updateInfo()
        }


    }

    private suspend fun InsertRecords() {
        val retrofit = MyToken(this).retrofit

        val service = retrofit.create(RecordService::class.java)

        val insertExerciseRequest = InsertExerciseRequest(0,hb,0,energy,
            endtime,mile,elapsedTime,endtime,"running",0,"",0,starttime,0,endtime)

        Log.i("insertExerciseRequest:",insertExerciseRequest.toString())

        val call: Call<InsertExerciseResponse> = service.insertExerciseRecord(insertExerciseRequest)
        call.enqueue(object : Callback<InsertExerciseResponse> {
            override fun onResponse(call: Call<InsertExerciseResponse>, response: Response<InsertExerciseResponse>) {
                if (response.isSuccessful()) {
                    val insertExerciseResponse: InsertExerciseResponse? = response.body()
                    // 处理响应数据
                    if (insertExerciseResponse != null) {
                        // 根据 code 值判断处理逻辑
                        if (insertExerciseResponse.code == 1) {
                            showAlertDialog("新增记录："+String.format("%02d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, elapsedTime % 60))
                            // 重置数据
                            elapsedTime = 0
                            mile = 0F
                            speed = 0F
                            hb = 0
                            energy = 0F
                            starttime = ""
                            endtime = ""

                            shouldDrawPath = false
                            lastPoint = null
                            currentPoint = null

                            aMap!!.clear()
                            pathPoints.clear()
                            aMap!!.isMyLocationEnabled = true
                            aMap!!.animateCamera(CameraUpdateFactory.zoomTo(19F))
                            updateInfo()
                        } else {
                            showAlertDialog(insertExerciseResponse.message)
                        }
                        Log.d("InsertExercise", "Response: " + insertExerciseResponse.toString())
                    }
                } else {
                    Log.e("InsertExercise", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<InsertExerciseResponse>, t: Throwable?) {
                Log.e("InsertExercise", "网络请求失败: ", t)
            }
        })
    }



    // 开始绘制轨迹
    private fun startTracking() {
        Log.d("Tracking", "Start tracking")
//        scope.launch {
//            locationClient.startLocation() // 在后台线程启动定位
//        }
        locationClient!!.startLocation()
    }

    // 暂停绘制轨迹
    private fun pauseTracking() {
        Log.d("Tracking", "Pause tracking")
//        scope.launch {
//            locationClient.stopLocation() // 在后台线程暂停定位
//        }
        locationClient!!.stopLocation()

    }


    override fun onLocationChanged(location: AMapLocation?) {
        location?.let {
            if (it.errorCode == 0 && isRunning) {
                // 定位成功，更新当前点
                currentPoint = LatLng(it.latitude, it.longitude)
                pathPoints.add(currentPoint!!)

                // 如果当前点和前一个点都存在并且 shouldDrawPath 为 true，则累加距离并绘制路径
                if (lastPoint != null && currentPoint != null && shouldDrawPath) {
                    // 计算两点之间的距离并累加到总距离
                    mile += AMapUtils.calculateLineDistance(lastPoint, currentPoint)

                    // 在主线程上绘制路径
                    runOnUiThread { drawPath(lastPoint!!, currentPoint!!) }

                    Log.d("LocationSuccess_Running", "Location_Running: ${it.latitude}, ${it.longitude}, Distance: $mile")
                }
                // 更新 lastPoint 为当前点
                lastPoint = currentPoint

                shouldDrawPath = true

            } else {
                // 定位失败
                Log.e("LocationError_Running", "Location_Running error: ${it.errorCode}, ${it.errorInfo}")
            }
        }
    }

    // 绘制从 lastPoint 到 currentPoint 的路径
    private fun drawPath(from: LatLng, to: LatLng) {
        // 创建新的 PolylineOptions 只绘制两个点之间的路径
        val polylineOptions = PolylineOptions()
            .add(from, to)
            .width(10f)
            .color(Color.BLUE)

        // 添加到地图
        aMap!!.addPolyline(polylineOptions)

        aMap!!.isMyLocationEnabled = true
        aMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(to, 19F))
    }


    // 面板上更新数据
    private fun updateInfo(){
        useTime = findViewById(R.id.UseTime)
        runMile = findViewById(R.id.runMile)
        runSpeed = findViewById(R.id.runSpeed)
        heartBeat = findViewById(R.id.heartBeat)
        runEnergy = findViewById(R.id.runEnergy)

        val hours = elapsedTime / 3600
        val minutes = (elapsedTime % 3600) / 60
        val seconds = elapsedTime % 60

        useTime.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        if(elapsedTime == 0){
            runMile.text = "0km"
            speed = 0F
        }else{
            if(mile >1000){
                val km = mile / 1000
                speed = (km / (elapsedTime.toFloat() / 3600.0 )).toFloat()
                runMile.text = String.format("%.1f", km)+"km"
            }else{
                speed = ( mile / (elapsedTime.toFloat() / 3.6 )).toFloat()
                runMile.text = String.format("%.1f", mile)+"m"
            }
        }

        energy = calculateCaloriesBurned(elapsedTime, mile, 65F)

        runSpeed.text = String.format("%.2f", speed)+ "km/h"
        heartBeat.text = hb.toString() + "bpm"
        runEnergy.text = String.format("%.2f", energy)+ "kcal"

    }

    private fun calculateCaloriesBurned(timeInSeconds: Int, distanceInKm: Float, weightInKg: Float): Float {
        // 计算速度
        val speedInKmH = distanceInKm / (timeInSeconds / 3600.0)

        // 根据速度选择 MET 值
        val metValue = when {
            speedInKmH <= 8.0F -> 8.3F
            speedInKmH <= 9.7F -> 9.8F
            speedInKmH <= 11.3F -> 11.4F
            speedInKmH <= 12.9F -> 12.8F
            speedInKmH <= 14.5F -> 14.5F
            else -> 16.0F
        }

        // 计算时间（小时）
        val timeInHours = timeInSeconds / 3600F

        // 计算消耗的卡路里
        return metValue * weightInKg * timeInHours
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
}