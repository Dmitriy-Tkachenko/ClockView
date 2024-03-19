package ru.tk4dmitriy.customclockview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

private const val FRAME_TO_RADIUS_RATIO = 2.0f / 21.0f

private const val MAIN_LONG_PART_SECOND_HAND_TO_RADIUS_RATIO = 32.0f / 45.0f
private const val MAIN_SHORT_PART_SECOND_HAND_TO_RADIUS_RATIO = 2.0f / 35.0f
private const val ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO = 5.0f / 21.0f
private const val WIDTH_MAIN_PART_SECOND_HAND_TO_RADIUS_RATIO = 1.0f / 63.0f
private const val WIDTH_ADDITIONAL_PART_SECOND_HAND_TO_RADIUS_RATIO = 1.0f / 35.0f

private const val LONG_PART_MINUTE_HAND_TO_RADIUS_RATIO = 29.0f / 45.0f
private const val SHORT_PART_MINUTE_HAND_TO_RADIUS_RATIO = 5.0f / 21.0f
private const val WIDTH_MINUTE_HAND_TO_RADIUS_RATIO = 2.0f / 45.0f

private const val LONG_PART_HOUR_HAND_TO_RADIUS_RATIO = 16.0f / 35.0f
private const val SHORT_PART_HOUR_HAND_TO_RADIUS_RATIO = 5.0f / 21.0f
private const val WIDTH_HOUR_HAND_TO_RADIUS_RATIO = 4.0f / 63.0f

private const val RADIUS_NON_HOUR_MARKER_TO_RADIUS_RATIO = 3.0f / 315.0f
private const val RADIUS_HOUR_MARKER_TO_RADIUS_RATIO = 4.0f / 315.0f
private const val POS_CLOCK_MARKER_TO_RADIUS_RATIO = 52.0f / 63.0f

private const val HOUR_LABEL_SIZE_TO_RADIUS_RATIO = 5.0f / 21.0f
private const val HOR_POS_HOUR_LABEL_TO_RADIUS_RATIO = 44.0f / 63.0f
private const val VERT_POS_HOUR_LABEL_TO_RADIUS_RATIO = 2.0f / 3.0f

private const val SECOND_TO_ANGLE_RATIO = Math.PI / 30
private const val SECOND_TO_MINUTE_ANGLE_RATIO = Math.PI / (30 * 60)
private const val SECOND_TO_HOUR_ANGLE_RATIO = Math.PI / (30 * 60 * 12)
private const val OFFSET_ANGLE = Math.PI / 2

private const val INVALIDATE_PERIOD = 160L

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mainPartSecondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val additionalPartSecondHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clockMarkersPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hourLabelsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hourRect = Rect()

    private val defWidth: Int = resources.getDimension(R.dimen.defWidth).toInt()
    private val defHeight: Int = resources.getDimension(R.dimen.defHeight).toInt()

    private val calendar: Calendar = Calendar.getInstance()
    private var currSecond = 0
    private var currMinute = 0
    private var currHour = 0

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var frameRadius = 0f
    private var angle = 0f

    var baseColor = 0
        set(value) {
            basePaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }
    var frameColor = 0
        set(value) {
            framePaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }
    var secondHandColor = 0
        set(value) {
            mainPartSecondHandPaint.color = value
            additionalPartSecondHandPaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }
    var minuteHandColor = 0
        set(value) {
            minuteHandPaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }
    var hourHandColor = 0
        set(value) {
            hourHandPaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }
    var clockMarkersColor = 0
        set(value) {
            clockMarkersPaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }
    var hourLabelsColor = 0
        set(value) {
            hourLabelsPaint.color = value
            if (field != value && field != 0) invalidate()
            field = value
        }

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
                R.styleable.ClockView_secondHandColor,
                ContextCompat.getColor(context, R.color.black)
            )
            minuteHandColor = getColor(
                R.styleable.ClockView_minuteHandColor,
                ContextCompat.getColor(context, R.color.black)
            )
            hourHandColor = getColor(
                R.styleable.ClockView_hourHandColor,
                ContextCompat.getColor(context, R.color.black)
            )
            clockMarkersColor = getColor(
                R.styleable.ClockView_clockMarkersColor,
                ContextCompat.getColor(context, R.color.black)
            )
            hourLabelsColor = getColor(
                R.styleable.ClockView_hourLabelsColor,
                ContextCompat.getColor(context, R.color.black)
            )
        }

        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES

        framePaint.apply { style = Paint.Style.STROKE }
        hourLabelsPaint.apply { typeface = Typeface.DEFAULT
            textAlign = Paint.Align.CENTER; letterSpacing = -0.16f; }
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
        minuteHandPaint.strokeWidth =
            radius * WIDTH_MINUTE_HAND_TO_RADIUS_RATIO
        hourHandPaint.strokeWidth =
            radius * WIDTH_HOUR_HAND_TO_RADIUS_RATIO

        hourLabelsPaint.textSize = radius * HOUR_LABEL_SIZE_TO_RADIUS_RATIO
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBase(canvas = canvas)
        drawFrame(canvas = canvas)
        drawClockMarkers(canvas = canvas)
        drawHourLabels(canvas = canvas)
        drawClockHands(canvas = canvas)
        postInvalidateDelayed(INVALIDATE_PERIOD)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = resolveSize(defWidth, widthMeasureSpec)
        val height = resolveSize(defHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun drawBase(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, basePaint)
    }

    private fun drawFrame(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, frameRadius, framePaint)
    }

    private fun drawClockMarkers(canvas: Canvas) {
        for (second in 1.. 60) {
            angle = (second * SECOND_TO_ANGLE_RATIO - OFFSET_ANGLE).toFloat()
            canvas.drawCircle(
                centerX + cos(angle) * radius * POS_CLOCK_MARKER_TO_RADIUS_RATIO,
                centerY + sin(angle) * radius * POS_CLOCK_MARKER_TO_RADIUS_RATIO,
                radius * if (second % 5 == 0) RADIUS_HOUR_MARKER_TO_RADIUS_RATIO
                else RADIUS_NON_HOUR_MARKER_TO_RADIUS_RATIO,
                clockMarkersPaint
            )
        }
    }

    private fun drawHourLabels(canvas: Canvas) {
        for (hour in 1..12) {
            angle = (hour * 5 * SECOND_TO_ANGLE_RATIO - OFFSET_ANGLE).toFloat()
            hourLabelsPaint.getTextBounds("$hour", 0, "$hour".length, hourRect)
            canvas.drawText(
                "$hour",
                centerX + cos(angle) * radius * HOR_POS_HOUR_LABEL_TO_RADIUS_RATIO,
                centerY + (hourRect.height() / 2) + sin(angle) *
                        radius * VERT_POS_HOUR_LABEL_TO_RADIUS_RATIO,
                hourLabelsPaint
            )
        }
    }

    private fun drawClockHands(canvas: Canvas) {
        calendar.timeInMillis = System.currentTimeMillis()
        currHour = calendar.get(Calendar.HOUR_OF_DAY)
        currHour = if (currHour > 12) currHour - 12 else currHour
        currMinute = calendar.get(Calendar.MINUTE)
        currSecond = calendar.get(Calendar.SECOND)
        drawHourHand(canvas = canvas, second = (currHour * 60 + currMinute) * 60 + currSecond)
        drawMinuteHand(canvas = canvas, second = currMinute * 60 + currSecond)
        drawSecondHand(canvas = canvas, second = currSecond)
    }

    private fun drawHourHand(canvas: Canvas, second: Int) {
        angle = (second * SECOND_TO_HOUR_ANGLE_RATIO - OFFSET_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * radius * SHORT_PART_HOUR_HAND_TO_RADIUS_RATIO,
            centerY - sin(angle) * radius * SHORT_PART_HOUR_HAND_TO_RADIUS_RATIO,
            centerX + cos(angle) * radius * LONG_PART_HOUR_HAND_TO_RADIUS_RATIO,
            centerY + sin(angle) * radius * LONG_PART_HOUR_HAND_TO_RADIUS_RATIO,
            hourHandPaint
        )
    }
    private fun drawMinuteHand(canvas: Canvas, second: Int) {
        angle = (second * SECOND_TO_MINUTE_ANGLE_RATIO - OFFSET_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * radius * SHORT_PART_MINUTE_HAND_TO_RADIUS_RATIO,
            centerY - sin(angle) * radius * SHORT_PART_MINUTE_HAND_TO_RADIUS_RATIO,
            centerX + cos(angle) * radius * LONG_PART_MINUTE_HAND_TO_RADIUS_RATIO,
            centerY + sin(angle) * radius * LONG_PART_MINUTE_HAND_TO_RADIUS_RATIO,
            minuteHandPaint
        )
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

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.contentDescription = getCurrentTimeDescription()
    }

    private fun getCurrentTimeDescription() =
        String.format(Locale.getDefault(), "%02d:%02d:%02d", currHour, currMinute, currSecond)

    override fun onSaveInstanceState(): Parcelable {
        return SavedState(super.onSaveInstanceState()).apply {
            baseColor = this@ClockView.baseColor
            frameColor = this@ClockView.frameColor
            secondHandColor = this@ClockView.secondHandColor
            minuteHandColor = this@ClockView.minuteHandColor
            hourHandColor = this@ClockView.hourHandColor
            clockMarkersColor = this@ClockView.clockMarkersColor
            hourLabelsColor = this@ClockView.hourLabelsColor
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            frameColor = state.frameColor
            baseColor = state.baseColor
            frameColor = state.frameColor
            secondHandColor = state.secondHandColor
            minuteHandColor = state.minuteHandColor
            hourHandColor = state.hourHandColor
            clockMarkersColor = state.clockMarkersColor
            hourLabelsColor = state.hourLabelsColor
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState {
        var baseColor = 0
        var frameColor = 0
        var secondHandColor = 0
        var minuteHandColor = 0
        var hourHandColor = 0
        var clockMarkersColor = 0
        var hourLabelsColor = 0

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            baseColor = parcel.readInt()
            frameColor = parcel.readInt()
            secondHandColor = parcel.readInt()
            minuteHandColor = parcel.readInt()
            hourHandColor = parcel.readInt()
            clockMarkersColor = parcel.readInt()
            hourLabelsColor = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(baseColor)
            out.writeInt(frameColor)
            out.writeInt(secondHandColor)
            out.writeInt(minuteHandColor)
            out.writeInt(hourHandColor)
            out.writeInt(clockMarkersColor)
            out.writeInt(hourLabelsColor)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}