package com.example.easyscreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class CropView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        // 半透明黑色
        color = 0x80000000.toInt()
    }
    private val selectionRect = Rect()
    private var isSelecting = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isSelecting) {
            canvas.drawRect(selectionRect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isSelecting = true
                selectionRect.set(x, y, x, y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                selectionRect.right = x
                selectionRect.bottom = y
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                isSelecting = false
                invalidate()
                performCrop() // 可以在这里触发裁剪操作
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun performCrop() {
        // 获取选定的矩形区域
        val rect = getSelectionRect()
        if (rect.width() > 0 && rect.height() > 0) {
            // 调用 MainActivity 的回调函数进行裁剪
            (context as? MainActivity)?.cropScreenshot(rect)
        } else {
            Toast.makeText(context, "请选定裁剪区域", Toast.LENGTH_SHORT).show()
        }
    }

    fun getSelectionRect(): Rect {
        return selectionRect
    }
}