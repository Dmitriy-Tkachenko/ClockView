package ru.tk4dmitriy.customclockview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.math.min

private const val STROKE_WIDTH_RATIO = 10

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {
    private val defWidth: Int = resources.getDimension(R.dimen.defWidth).toInt()
    private val defHeight: Int = resources.getDimension(R.dimen.defHeight).toInt()

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var baseColor = 0
    var frameColor = 0

    init {
        context.withStyledAttributes(attrs, R.styleable.ClockView, R.attr.clockViewStyle) {
            baseColor = getColor(
                R.styleable.ClockView_baseColor,
                ContextCompat.getColor(context, R.color.white)
            )
            frameColor = getColor(
                R.styleable.ClockView_frameColor,
                ContextCompat.getColor(context, R.color.black)
            )
        }
    }

    private fun drawBase(canvas: Canvas) {
        paint.color = baseColor
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    private fun drawFrame(canvas: Canvas) {
        paint.color = frameColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = radius / STROKE_WIDTH_RATIO
        val adjustedRadius = radius - paint.strokeWidth / 2
        canvas.drawCircle(centerX, centerY, adjustedRadius, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = width.coerceAtMost(height) / 2f
        centerX = w / 2f
        centerY = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        drawBase(canvas = canvas)
        drawFrame(canvas = canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = resolveSize(defWidth, widthMeasureSpec)
        val height = resolveSize(defHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }
}