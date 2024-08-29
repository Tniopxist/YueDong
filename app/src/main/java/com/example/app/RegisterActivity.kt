package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val username: EditText =findViewById(R.id.registerAcc)
        val pwd:EditText=findViewById(R.id.registerPwd)
//        val confirm_pwd:EditText=findViewById(R.id.confirm_pwd)
        val inputCode:EditText =findViewById(R.id.inputCode)


//        验证，点击注册
        val register:Button =findViewById(R.id.btn_register)
        register.setOnClickListener{

            val str_name = username.text.toString()
            val str_pwd = pwd.text.toString()
            val str_code = inputCode.text.toString()

            //      手机号校验
            val username_regex:Regex= "[1][34578]\\d{9}".toRegex()
            //      密码校验:密码为6~16位数字,英文,符号至少两种组合的字符
            val pwd_regex:Regex="^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\\(\\)])+$)([^(0-9a-zA-Z)]|[\\(\\)]|[a-zA-Z]|[0-9]){6,16}$".toRegex()

            //        判断输入是否正确
            if (str_name.isEmpty()){
                username.setError("手机号不能为空")
            } else if (str_name.matches(username_regex)){

            }else{
                username.setError("请输入正确的手机号")
            }
            if (str_pwd.isEmpty()){
                pwd.setError("密码不能为空")
            }else if (str_pwd.matches(pwd_regex)){

            }else{
                pwd.setError("密码为6~16位数字,英文,符号至少两种组合的字符")
            }
//            if (str_confirm_pwd.isEmpty()){
//                confirm_pwd.setError("确认密码不能为空")
//            }
//            if (!str_confirm_pwd.equals(str_pwd)){
//                confirm_pwd.setError("两次密码必须保持一致")
//            }
        }

        //  点击登录，跳转到登录页面
        val login: TextView =findViewById(R.id.goLogin)
        login.setOnClickListener{
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}