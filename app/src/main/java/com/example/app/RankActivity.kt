package com.example.app

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RankActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            "排名", null, "昵称", "里程", "时长",
            isTitle = true
        )
        rootLayout.addView(titleRow)

        //获取第4-20名数据

        // 动态创建数据行
        for (i in 4..20) {
            val dataRow = createRow(
                "$i", "", "昵称 $i", "100$i", "200$i"
            )
            rootLayout.addView(dataRow)
        }

        scrollView.addView(rootLayout)
        setContentView(scrollView)
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

            //获取前三名数据


            // 第二名
            addView(createPodiumItem("2", "","昵称 2", "1002", "2002", heightMultiplier = 0.65f))

            // 第一名
            addView(createPodiumItem("1", "","昵称 1", "1001", "2001", heightMultiplier = 1.0f))

            // 第三名
            addView(createPodiumItem("3", "","昵称 3", "1003", "2003", heightMultiplier = 0.35f))
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
}
