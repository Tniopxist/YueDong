package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val username: EditText =findViewById(R.id.logAcc)
        val pwd:EditText=findViewById(R.id.logPwd)

//        验证，点击登录
        val login: Button =findViewById(R.id.btn_login)
        login.setOnClickListener{

            val str_name=username.text.toString()
            val str_pwd=pwd.text.toString()
//          判断输入是否正确
            if (str_name.isEmpty()){
                username.setError("手机号不能为空")
            }
            if (str_pwd.isEmpty()){
                pwd.setError("密码不能为空")
            }

//          登录成功，进入主页
            if (str_name.equals("13456789028") && str_pwd.equals("123")){
                val intent= Intent(this,ExerciseActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        }

        //       还未注册？去注册
        val register: TextView =findViewById(R.id.goRegister)
        register.setOnClickListener{
            Log.d("go_register", "to register")
            val intent=Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }
}