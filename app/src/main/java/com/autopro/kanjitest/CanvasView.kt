package com.autopro.kanjitest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View

internal class CanvasView(context: Context?) : View(context) {

    var paint: Paint = Paint()
    var path: Path = Path() // 자취를 저장할 객체

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(Int.MAX_VALUE, 800)
    }

    override fun onDraw(canvas: Canvas) { // 화면을 그려주는 메서드
        canvas.drawPath(path, paint) // 저장된 path 를 그려라
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(x, y) // 자취에 그리지 말고 위치만 이동해라
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y) // 자취에 선을 그려라
            MotionEvent.ACTION_UP -> {
            }
        }


        invalidate() // 화면을 다시그려라
        return true

    }

    init {
        paint.style = Paint.Style.STROKE // 선이 그려지도록
        paint.strokeWidth = 8f // 선의 굵기 지정
        paint.color = Color.BLACK
    }
}