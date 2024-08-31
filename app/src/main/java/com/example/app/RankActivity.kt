package com.example.app

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.app.data.UserDistanceRankData
import com.example.app.http.MyToken
import com.example.app.http.RankService
import com.example.app.model.RankRequest
import com.example.app.model.RankResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Instant

class RankActivity : AppCompatActivity() {
    lateinit var list:UserDistanceRankData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getRankList()
    }

    private fun setupUI(){
        // ScrollView
        val scrollView = ScrollView(this).apply {
            setBackgroundResource(R.drawable.home)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // 垂直方向的LinearLayout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(25, 16, 25, 16)  // 设置根布局的边距
                setPadding(10, 25, 10, 25)
            }
        }

        // 返回按钮
        val backButton = ImageView(this).apply {
            setImageResource(R.drawable.exit)
            layoutParams = LinearLayout.LayoutParams(
                75,
                75
            ).apply {
                gravity = Gravity.START
                setMargins(40, 40, 0, 40)
            }
            setOnClickListener {
                finish()  // 返回上一个Activity
            }
        }

        // 添加返回按钮到根布局
        rootLayout.addView(backButton)

        // 添加奖台布局
        val podiumLayout = createPodiumLayout()
        rootLayout.addView(podiumLayout)

        // 添加标题行
        val titleRow = createRow(
            "排名", null, "昵称", "Id", "里程(km)",
            isTitle = true
        )
        rootLayout.addView(titleRow)

        //获取第4-20名数据

        // 动态创建数据行
        for (i in 4..20) {
            var nickname = ""
            var img = ""
            var id = ""
            var distance = ""

            if(list.userDistanceData.size >= i){
                nickname = list.userDistanceData.get(i-1).nickName
                img = list.userDistanceData.get(i-1).headerImg
                id = list.userDistanceData.get(i-1).userId.toString()
                distance = list.userDistanceData.get(i-1).distance.toString()
            }

            val dataRow = createRow(
                "$i", img, nickname, id, distance
            )
            rootLayout.addView(dataRow)
        }

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    private fun nullRankUI(){
        // 垂直方向的LinearLayout
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(25, 16, 25, 16)  // 设置根布局的边距
                setPadding(10, 25, 10, 25)
            }
        }

        // 返回按钮
        val backButton = ImageView(this).apply {
            setImageResource(R.drawable.exit)
            layoutParams = LinearLayout.LayoutParams(
                75,
                75
            ).apply {
                gravity = Gravity.START
                setMargins(40, 40, 0, 40)
            }
            setOnClickListener {
                finish()  // 返回上一个Activity
            }
        }

        // 添加返回按钮到根布局
        rootLayout.addView(backButton)

        // 排名
        val textView = TextView(this@RankActivity).apply {
            text = "当前暂无排名"
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(15, 5, 0, 0)
            }
        }

        rootLayout.addView(textView)

        setContentView(rootLayout)
    }

    // 创建奖台布局
    private fun createPodiumLayout(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
            gravity = Gravity.CENTER

            var nickname1:String = ""
            var img1:String= ""
            var id1:String= ""
            var distance1:String= ""
            var nickname2 :String= ""
            var img2 :String= ""
            var id2 :String= ""
            var distance2 :String= ""
            var nickname3 :String= ""
            var img3 :String= ""
            var id3 :String= ""
            var distance3 :String= ""


            if(list.userDistanceData.size > 0 ) {
                //获取前三名数据
                nickname1 = list.userDistanceData.get(0).nickName
                img1 = list.userDistanceData.get(0).headerImg
                id1 = list.userDistanceData.get(0).userId.toString()
                distance1 = list.userDistanceData.get(0).distance.toString()

                if (list.userDistanceData.size > 1) {
                    nickname2 = list.userDistanceData.get(1).nickName
                    img2 = list.userDistanceData.get(1).headerImg
                    id2 = list.userDistanceData.get(1).userId.toString()
                    distance2 = list.userDistanceData.get(1).distance.toString()

                    if (list.userDistanceData.size > 2) {
                        nickname3 = list.userDistanceData.get(2).nickName
                        img3 = list.userDistanceData.get(2).headerImg
                        id3 = list.userDistanceData.get(2).userId.toString()
                        distance3 = list.userDistanceData.get(2).distance.toString()
                    }
                }
            }


            // 第二名
            addView(createPodiumItem("2", img2,nickname2, id2, distance2, heightMultiplier = 0.65f))

            // 第一名
            addView(createPodiumItem("1", img1,nickname1, id1, distance1, heightMultiplier = 1.0f))

            // 第三名
            addView(createPodiumItem("3", img3,nickname3, id3, distance3, heightMultiplier = 0.35f))
        }
    }


    // 创建奖台上的每一项
    private fun createPodiumItem(
        rank: String,
        avatar: String?,
        nickname: String,
        data1: String,
        data2: String,
        heightMultiplier: Float
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                setMargins(16, 100-(100 * heightMultiplier).toInt(), 16, 0)
            }
            gravity = Gravity.CENTER_HORIZONTAL

            // 排名
            val rankTextView = TextView(this@RankActivity).apply {
                text = rank
                textSize = 26f
                gravity = Gravity.CENTER
                setBold(this)
            }

            // 头像
            val avatarImageView = ImageView(this@RankActivity).apply {
                setImageResource(R.drawable.boy)
                layoutParams = LinearLayout.LayoutParams(((heightMultiplier + 0.55 ) * 130).toInt(), ((heightMultiplier + 0.55 ) * 130).toInt()).apply {
                    setMargins(0, 16, 0, 16)
                }
            }

            // 昵称
            val nicknameTextView = TextView(this@RankActivity).apply {
                text = nickname
                textSize = 20f
                gravity = Gravity.CENTER
            }

            // 里程
            val data1TextView = TextView(this@RankActivity).apply {
                text = data1
                textSize = 18f
                gravity = Gravity.CENTER
            }

            // 时长
            val data2TextView = TextView(this@RankActivity).apply {
                text = data2
                textSize = 12f
                gravity = Gravity.CENTER
            }

            // 按顺序添加各个元素到当前奖台项
            addView(rankTextView)
            addView(avatarImageView)
            addView(nicknameTextView)
            addView(data1TextView)
            addView(data2TextView)
        }
    }


    private fun createRow(
        rank: String,
        avatar: String?,
        nickname: String,
        data1: String,
        data2: String,
        isTitle: Boolean = false
    ): LinearLayout {
        // 创建一行
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                if (!isTitle) {
                    setMargins(40, 25, 25, 25)  // 设置每行之间的间距
                }
            }
            gravity = Gravity.CENTER_VERTICAL

            // 排名
            val rankTextView = TextView(this@RankActivity).apply {
                text = rank
                textSize = if (isTitle) 20f else 18f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                if (isTitle) setBold(this)
            }

            // 头像
            val avatarImageView = ImageView(this@RankActivity).apply {
                if (avatar != null) {
                    setImageResource(R.drawable.boy)
                }
                layoutParams = LinearLayout.LayoutParams(110, 110).apply {
                    setMargins(8, 0, 8, 0)
                }
            }

            // 昵称
            val nicknameTextView = TextView(this@RankActivity).apply {
                text = nickname
                textSize = if (isTitle) 20f else 18f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2f
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                if (isTitle) setBold(this)
            }

            // 里程
            val data1TextView = TextView(this@RankActivity).apply {
                text = data1
                textSize = if (isTitle) 20f else 18f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2f
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                if (isTitle) setBold(this)
            }

            // 时长
            val data2TextView = TextView(this@RankActivity).apply {
                text = data2
                textSize = if (isTitle) 20f else 18f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2f
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                if (isTitle) setBold(this)
            }

            // 将元素添加到当前行
            addView(rankTextView)
            addView(avatarImageView)
            addView(nicknameTextView)
            addView(data1TextView)
            addView(data2TextView)
        }
    }

    private fun setBold(textView: TextView) {
        textView.paint.isFakeBoldText = true
    }

    private  fun getRankList() {
        val retrofit = MyToken(this).retrofit
        val service = retrofit.create(RankService::class.java)

        val nowtime = Instant.now().toString()

        val rankRequest = RankRequest(nowtime,"",1,20)
        Log.i("RankRequest", rankRequest.toString())

        val call: Call<RankResponse> = service.getRankList(rankRequest)
        call.enqueue(object : Callback<RankResponse> {
            override fun onResponse(call: Call<RankResponse>, response: Response<RankResponse>) {
                if (response.isSuccessful()) {
                    val rankResponse: RankResponse? = response.body()
                    // 处理响应数据
                    if (rankResponse != null) {
                        // 根据 code 值判断处理逻辑
                        if (rankResponse.code == 1) {
                            if(rankResponse.data.list != null){
                                list = Gson().fromJson(rankResponse.data.list,UserDistanceRankData::class.java)
                                // 当数据成功获取后，初始化 UI
                                setupUI()
                            }else{
                                showAlertDialog("当前排行榜为空")
                                nullRankUI()
                            }
                        } else {
                            showAlertDialog(rankResponse.message)
                        }
                        Log.d("RankList", "Response: " + rankResponse.toString())
                    }
                } else {
                    Log.e("RankList", "请求失败: " + response.message())
                }
            }

            override fun onFailure(call: Call<RankResponse>, t: Throwable) {
                Log.e("RankList", "网络请求失败: ", t)
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
