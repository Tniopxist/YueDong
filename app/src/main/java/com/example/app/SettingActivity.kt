package com.example.app

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.http.MyToken
import com.example.app.http.RecordService
import com.example.app.http.UserService
import com.example.app.model.GetUserRequest
import com.example.app.model.GetUserResponse
import com.example.app.model.InsertExerciseRequest
import com.example.app.model.InsertExerciseResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getUserInfo()

        findViewById<ImageView>(R.id.exit).setOnClickListener {
            finishAffinity() // 关闭所有Activity
            System.exit(0)   // 结束进程，完全退出应用程序
        }

        findViewById<TextView>(R.id.toMyself).setOnClickListener {
            val intent = Intent(this, MyselfActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.navHealth).setOnClickListener { startActivity(Intent(this,HealthyActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navExercise).setOnClickListener { startActivity(Intent(this,ExerciseActivity::class.java)) }
        findViewById<LinearLayout>(R.id.navSetting).setOnClickListener { startActivity(Intent(this,SettingActivity::class.java)) }
    }

    private fun getUserInfo(){
        val user_img = findViewById<ImageView>(R.id.avatar_img)
        val user_name = findViewById<TextView>(R.id.userName)
        val user_id = findViewById<TextView>(R.id.Uid)

        val retrofit = MyToken(this).retrofit

        val service = retrofit.create(UserService::class.java)

        val getUserRequest = GetUserRequest()

        Log.i("getUserRequest:",getUserRequest.toString())

        val call: Call<GetUserResponse> = service.getUserInfo()
        call.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(call: Call<GetUserResponse>, response: Response<GetUserResponse>) {
                if (response.isSuccessful()) {
                    val getUserResponse: GetUserResponse? = response.body()
                    // 处理响应数据
                    if (getUserResponse != null) {
                        // 根据 code 值判断处理逻辑
                        if (getUserResponse.code == 1) {
                            getImg(getUserResponse.data.headerImg,user_img)
//                            user_img.setImageURI(getUserResponse.data.headerImg.toUri())
                            user_name.setText(getUserResponse.data.nickname)
                            user_id.setText(getUserResponse.data.username)
                        }
                        Log.d("GetUserResponse", "Response: " + getUserResponse.toString())
                    }
                } else {
                    Log.e("GetUserResponse", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable?) {
                Log.e("GetUserResponse", "网络请求失败: ", t)
            }
        })

    }

    private fun getImg(string: String,iv:ImageView) {
        val url = "http://101.42.43.59:8888/" + string

        // 使用新线程执行网络操作
        thread {
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // 在主线程更新UI
                runOnUiThread {
                    iv.setImageBitmap(bitmap)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}