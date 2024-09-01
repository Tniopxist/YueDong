package com.example.app

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.app.http.MyToken
import com.example.app.http.UserService
import com.example.app.model.GetUserRequest
import com.example.app.model.GetUserResponse
import com.example.app.model.UpdatePWRequest
import com.example.app.model.UpdatePWResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdatePwActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_pw)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_update_pw)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val oldPasswordEditText = findViewById<EditText>(R.id.et_old_password)
        val newPasswordEditText = findViewById<EditText>(R.id.et_new_password)
        val confirmButton = findViewById<Button>(R.id.btn_confirm_change)

        // 设置按钮点击监听器
        confirmButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            // 校验输入
            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                updatePw(newPassword,oldPassword)
            } else {
                Toast.makeText(this, "请输入完整的密码信息", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.exit).setOnClickListener{
            finish()
        }

    }

    private fun updatePw(newPassword:String,oldPassword:String){
        val retrofit = MyToken(this).retrofit

        val service = retrofit.create(UserService::class.java)

        val updatePWRequest = UpdatePWRequest(newPassword,oldPassword)

        Log.i("updatePWRequest:",updatePWRequest.toString())

        val call: Call<UpdatePWResponse> = service.updatePw(updatePWRequest)
        call.enqueue(object : Callback<UpdatePWResponse> {
            override fun onResponse(call: Call<UpdatePWResponse>, response: Response<UpdatePWResponse>) {
                if (response.isSuccessful()) {
                    val updatePWResponse: UpdatePWResponse? = response.body()
                    // 处理响应数据
                    if (updatePWResponse != null) {
                        // 根据 code 值判断处理逻辑
                        if (updatePWResponse.code == 1) {
                            showAlertDialog("密码修改成功")
                            val oldPasswordEditText = findViewById<EditText>(R.id.et_old_password)
                            val newPasswordEditText = findViewById<EditText>(R.id.et_new_password)
                            oldPasswordEditText.setText("")
                            newPasswordEditText.setText("")
                        }else{
                            showAlertDialog(updatePWResponse.toString())
                        }
                        Log.d("updatePWResponse", "Response: " + updatePWResponse.toString())
                    }
                } else {
                    Log.e("updatePWResponse", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<UpdatePWResponse>, t: Throwable?) {
                Log.e("updatePWResponse", "网络请求失败: ", t)
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