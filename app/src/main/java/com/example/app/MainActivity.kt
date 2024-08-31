package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.MapsInitializer
import com.example.app.http.BaseService
import com.example.app.model.LoginRequest
import com.example.app.model.LoginResponse
import com.example.app.model.RegisterRequest
import com.example.app.model.RegisterResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val goRegister = findViewById<TextView>(R.id.goRegister)
        goRegister.setOnClickListener {
            // 创建Intent对象来启动SecondActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                loginWithCode()
            }
        }
    }

    private suspend fun loginWithCode(){
        val logAcc = findViewById<EditText>(R.id.logAcc).text.toString()
        val logPwd = findViewById<EditText>(R.id.logPwd).text.toString()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://101.42.43.59:8888/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val service = retrofit.create(BaseService::class.java)

        val loginRequest = LoginRequest("","", logPwd, logAcc)
        Log.i("loginRequest", loginRequest.toString())
        val call: Call<LoginResponse> = service.loginWithCode(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful()) {
                    val loginResponse: LoginResponse? = response.body()
                    // 处理响应数据
                    if (loginResponse != null) {
                        // 根据 code 值判断处理逻辑
                        if (loginResponse.code == 1) {

                            // 存储 token 到 SharedPreferences
                            val token = loginResponse.data.token
                            val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", token)
                            editor.apply()
                            Log.i("Token:",token)

                            // 跳转到 HealthyActivity
                            val intent = Intent(this@MainActivity, HealthyActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 弹窗提示
                            showAlertDialog(loginResponse.message)
                        }
                        Log.d("loginWithCode", "Response: " + loginResponse.toString())
                    }
                } else {
                    Log.e("loginWithCode", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable?) {
                Log.e("loginWithCode", "网络请求失败: ", t)
            }
        })
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
}