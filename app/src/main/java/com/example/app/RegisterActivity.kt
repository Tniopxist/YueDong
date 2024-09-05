package com.example.app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.http.BaseService
import com.example.app.model.EmailRequest
import com.example.app.model.EmailResponse
import com.example.app.model.RegisterRequest
import com.example.app.model.RegisterResponse
import com.example.app.util.DrawableUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class RegisterActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var countdownTime = 60
    private val updateInterval = 1000L // 1s

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val goLogin = findViewById<TextView>(R.id.goLogin)
        goLogin.setOnClickListener {
            // 创建Intent对象来启动SecondActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val code = findViewById<TextView>(R.id.code)
        code.setOnClickListener {
            code.isClickable = false
            startCountdown()
            CoroutineScope(Dispatchers.Main).launch {
                getVerificationCode()
            }
        }

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                registerWithCode()
            }
        }

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_visibility_off) ?: return
        val scaledDrawable = DrawableUtils.scaleDrawable(this, drawable, 24, 24)
        findViewById<EditText>(R.id.registerPwd).setCompoundDrawablesWithIntrinsicBounds(null, null, scaledDrawable, null)

        val editText = findViewById<EditText>(R.id.registerPwd)
        val drawableRight = editText.compoundDrawables[2] // 右侧Drawable
        editText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (drawableRight != null) {
                    val drawableWidth = drawableRight.intrinsicWidth
                    val touchX = event.x.toInt()
                    if (touchX >= (editText.width - drawableWidth) && touchX <= editText.width) {
                        if (editText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                            editText.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        } else {
                            editText.setInputType(InputType.TYPE_CLASS_TEXT or  InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        }
                        editText.setSelection(editText.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun startCountdown() {
        val codeButton = findViewById<TextView>(R.id.code)
        codeButton.text = "60s后重新发送"
        handler.postDelayed(object : Runnable {
            override fun run() {
                countdownTime--
                if (countdownTime > 0) {
                    codeButton.text = "${countdownTime}s后重新发送"
                    handler.postDelayed(this, updateInterval)
                } else {
                    codeButton.text = "获取验证码"
                    codeButton.isClickable = true
                    countdownTime = 60 // 重置倒计时时间
                }
            }
        }, updateInterval)
    }

    private suspend fun getVerificationCode(){
        val emailEditText = findViewById<EditText>(R.id.registerAcc)
        val email = emailEditText.text.toString()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://101.42.43.59:8888/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val service = retrofit.create(BaseService::class.java)

        val emailRequest = EmailRequest(email)
        val call: Call<EmailResponse> = service.getVerificationCode(emailRequest)
        call.enqueue(object : Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful()) {
                    val emailResponse: EmailResponse? = response.body()
                    // 处理响应数据
                    if (emailResponse != null) {
                        Log.d("getVerificationCode", "Response: " + emailResponse.toString())
                    }
                } else {
                    Log.e("getVerificationCode", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable?) {
                Log.e("getVerificationCode", "网络请求失败: ", t)
            }
        })
    }

    private suspend fun registerWithCode(){
        val registerAcc = findViewById<EditText>(R.id.registerAcc).text.toString()
        val inputCode = findViewById<EditText>(R.id.inputCode).text.toString()
        val registerPwd = findViewById<EditText>(R.id.registerPwd).text.toString()


        val retrofit = Retrofit.Builder()
            .baseUrl("http://101.42.43.59:8888/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val service = retrofit.create(BaseService::class.java)

        val registerRequest = RegisterRequest(inputCode, registerAcc, registerPwd, "12345678900", registerAcc)
        Log.i("registerRequest", registerRequest.toString())
        val call: Call<RegisterResponse> = service.registerWithCode(registerRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful()) {
                    val registerResponse: RegisterResponse? = response.body()
                    // 处理响应数据
                    if (registerResponse != null) {
                        if (registerResponse.code == 1) {
                            // 跳转到 MainActivity
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 弹窗提示
                            showAlertDialog(registerResponse.message)
                        }
                        Log.d("registerWithCode", "Response: " + registerResponse.toString())
                    }
                } else {
                    Log.e("registerWithCode", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable?) {
                Log.e("registerWithCode", "网络请求失败: ", t)
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