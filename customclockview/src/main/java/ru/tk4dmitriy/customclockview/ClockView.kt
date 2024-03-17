package ru.tk4dmitriy.customclockview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {
    private val defWidth: Int = resources.getDimension(R.dimen.defWidth).toInt()
    private val defHeight: Int = resources.getDimension(R.dimen.defHeight).toInt()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }

    var baseColor = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.ClockView, R.attr.clockViewStyle) {
            baseColor = getColor(
                R.styleable.ClockView_baseColor,
                ContextCompat.getColor(context, R.color.black)
            )
        }
    }

    private fun drawBase(canvas: Canvas) {
        paint.color = baseColor
        canvas.drawCircle(width / 2f, height / 2f, width.coerceAtMost(height) / 2f, paint)
    }

    override fun onDraw(canvas: Canvas) {
        drawBase(canvas = canvas)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = resolveSize(defWidth, widthMeasureSpec)
        val height = resolveSize(defHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }
}