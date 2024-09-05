package com.example.app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.app.http.MyToken
import com.example.app.http.UserService
import com.example.app.model.GetUserRequest
import com.example.app.model.GetUserResponse
import com.example.app.model.UpdateUserRequest
import com.example.app.model.UpdateUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyselfActivity : AppCompatActivity() {
    lateinit var username : TextView
    lateinit var email: EditText
    lateinit var gender: EditText
    lateinit var nickname: EditText
    lateinit var phone: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_myself)

        getUserInfo()

        // 修改密码
        findViewById<Button>(R.id.button2).setOnClickListener{
            startActivity(Intent(this,UpdatePwActivity::class.java))
        }

        // 修改信息
        findViewById<Button>(R.id.buttonSave).setOnClickListener{
            updateUserInfo()
        }

        findViewById<ImageView>(R.id.exitmyself).setOnClickListener{
            finish()
        }

    }

    private fun getUserInfo(){
        username = findViewById(R.id.accEdit)
        email = findViewById(R.id.ageEdit)
        gender = findViewById(R.id.sexEdit)
        nickname = findViewById(R.id.nameEdit)
        phone = findViewById(R.id.heightEdit)

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
                            username.setText(getUserResponse.data.username)
                            email.setText(getUserResponse.data.email)
                            gender.setText(getUserResponse.data.gender)
                            nickname.setText(getUserResponse.data.nickname)
                            phone.setText(getUserResponse.data.phone)
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

    private fun updateUserInfo(){
        val retrofit = MyToken(this).retrofit

        val service = retrofit.create(UserService::class.java)

        val updateUserRequest = UpdateUserRequest(email.text.toString(),gender.text.toString(),nickname.text.toString(),phone.text.toString())

        Log.i("updateUserRequest:",updateUserRequest.toString())

        val call: Call<UpdateUserResponse> = service.updateUserInfo(updateUserRequest)
        call.enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if (response.isSuccessful()) {
                    val updateUserResponse: UpdateUserResponse? = response.body()
                    // 处理响应数据
                    if (updateUserResponse != null) {
                        // 根据 code 值判断处理逻辑
                        if (updateUserResponse.code == 1) {
                            if (!isFinishing && !isDestroyed) {
                                showAlertDialog(response.message())
                            }
                        }
                        Log.d("UpdateUserResponse", "Response: " + updateUserResponse.toString())
                    }
                } else {
                    Log.e("UpdateUserResponse", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable?) {
                Log.e("UpdateUserResponse", "网络请求失败: ", t)
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