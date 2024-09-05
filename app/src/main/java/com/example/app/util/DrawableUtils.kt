package com.example.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

object DrawableUtils {

    /**
     * 缩放 Drawable 到指定的宽度和高度（以 dp 为单位）。
     *
     * @param context 上下文
     * @param drawable 需要缩放的 Drawable 对象
     * @param widthDp 目标宽度（以 dp 为单位）
     * @param heightDp 目标高度（以 dp 为单位）
     * @return 缩放后的 Drawable 对象
     */
    fun scaleDrawable(context: Context, drawable: Drawable, widthDp: Int, heightDp: Int): Drawable {
        val density = context.resources.displayMetrics.density
        val widthPx = (widthDp * density).toInt()
        val heightPx = (heightDp * density).toInt()

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return BitmapDrawable(context.resources, bitmap)
    }
}