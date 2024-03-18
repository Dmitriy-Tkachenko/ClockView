package ru.tk4dmitriy.customclockview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.sin

private const val FRAME_TO_RADIUS_RATIO = 2.0f / 21.0f
private const val MAIN_LONG_PART_SECOND_HAND_TO_RADIUS_RATIO = 32.0f / 45.0f
private const val MAIN_SHORT_PART_SECOND_HAND_TO_RADIUS_RATIO = 2.0f / 35.0f
private const val ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO = 9.0f / 35.0f
private const val WIDTH_MAIN_PART_SECOND_HAND_TO_RADIUS_RATIO = 1.0f / 63.0f
private const val WIDTH_ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO = 1.0f / 35.0f
private const val SECOND_TO_ANGLE_RATIO = Math.PI / 30
private const val OFFSET_ANGLE = Math.PI / 2

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mainPartSecondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val additionalPartSecondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val defWidth: Int = resources.getDimension(R.dimen.defWidth).toInt()
    private val defHeight: Int = resources.getDimension(R.dimen.defHeight).toInt()

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var frameRadius = 0f
    private var angle = 0f

    var baseColor = 0
    var frameColor = 0
    var secondHandColor = 0

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
            secondHandColor = getColor(
                R.styleable.ClockView_frameColor,
                ContextCompat.getColor(context, R.color.black)
            )
        }
        basePaint.apply {
            color = baseColor
        }
        framePaint.apply {
            color = frameColor
            style = Paint.Style.STROKE
        }
        mainPartSecondHandPaint.apply {
            color = secondHandColor
        }
        additionalPartSecondHandPaint.apply {
            color = secondHandColor
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = width.coerceAtMost(height) / 2f
        centerX = w / 2f
        centerY = h / 2f

        framePaint.strokeWidth = radius * FRAME_TO_RADIUS_RATIO
        frameRadius = radius - framePaint.strokeWidth / 2

        mainPartSecondHandPaint.strokeWidth =
            radius * WIDTH_MAIN_PART_SECOND_HAND_TO_RADIUS_RATIO
        additionalPartSecondHandPaint.strokeWidth =
            radius * WIDTH_ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO
    }

    private fun drawBase(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, basePaint)
    }

    private fun drawFrame(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, frameRadius, framePaint)
    }
    private fun drawSecondHand(canvas: Canvas, second: Int) {
        angle = (second * SECOND_TO_ANGLE_RATIO - OFFSET_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * radius * MAIN_SHORT_PART_SECOND_HAND_TO_RADIUS_RATIO,
            centerY - sin(angle) * radius * MAIN_SHORT_PART_SECOND_HAND_TO_RADIUS_RATIO,
            centerX + cos(angle) * radius * MAIN_LONG_PART_SECOND_HAND_TO_RADIUS_RATIO,
            centerY + sin(angle) * radius * MAIN_LONG_PART_SECOND_HAND_TO_RADIUS_RATIO,
            mainPartSecondHandPaint
        )
        canvas.drawLine(
            centerX - cos(angle) * radius * ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO,
            centerY - sin(angle) * radius * ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO,
            centerX - cos(angle) * radius * MAIN_SHORT_PART_SECOND_HAND_TO_RADIUS_RATIO,
            centerY - sin(angle) * radius * MAIN_SHORT_PART_SECOND_HAND_TO_RADIUS_RATIO,
            additionalPartSecondHandPaint
        )
    }

    override fun onDraw(canvas: Canvas) {
        drawBase(canvas = canvas)
        drawFrame(canvas = canvas)
        drawSecondHand(canvas = canvas, second = 1)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = resolveSize(defWidth, widthMeasureSpec)
        val height = resolveSize(defHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }
}