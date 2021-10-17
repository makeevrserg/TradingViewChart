package com.makeevrserg.tradingviewchart.chart


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.makeevrserg.tradingviewchart.R
import java.lang.Exception
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

class KChart(context: Context, val attrs: AttributeSet?) : View(context, attrs) {


    var mCandleBodySize: Float = dpToPx(10).toFloat()
    var mCandleStringSize: Float = dpToPx(3).toFloat()
    var mTextSize: Float = spToPx(12f).toFloat()
    var mTextColor: Int = Color.parseColor("#303030")
    var mMinScaleX: Float = dpToPx(300).toFloat()
    var mMinScaleY: Float = dpToPx(300).toFloat()
    var mMaxScaleY: Float = dpToPx(2000).toFloat()
    var mMaxScaleX: Float = dpToPx(2000).toFloat()
    var _mScaleX: Float = dpToPx(1300).toFloat()
    var _mScaleY: Float = dpToPx(1300).toFloat()
    var mSpaceBetweenText: Float = dpToPx(10).toFloat()


    /**
     * Инстанс свечи
     */
    private var candlePaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.SQUARE
        this.strokeJoin = Paint.Join.MITER
        isAntiAlias = true
    }

    /**
     * Возвращает тело свечу при повышении
     */
    fun candleHighPaint() = candlePaint.apply {
        strokeWidth = mCandleBodySize
        color = Color.parseColor("#346beb")
    }


    /**
     * Возвращает нить свечи при повышении
     */
    fun candleHighPaintSmall() = candlePaint.apply {

        strokeWidth = mCandleStringSize
        color = Color.parseColor("#346beb")
    }


    /**
     * Вовзращает тело свечи при понижении
     */
    fun candleLowPaint() = candlePaint.apply {
        strokeWidth = mCandleBodySize
        color = Color.parseColor("#a83246")
    }

    /**
     * Вовзращает нить свечи при понижении
     */
    fun candleLowPaintSmall() = candlePaint.apply {
        strokeWidth = mCandleStringSize
        color = Color.parseColor("#a83246")
    }

    /**
     * Pain для горизонтального и вертикального текста
     */
    private var _textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }
    fun textPaint() = _textPaint.apply {
        textSize = mTextSize
        color = mTextColor
    }

    /**
     * Параметр хранит перемещение по Y
     */
    private var scrolledY = 0

    /**
     * Параметр хранит перемещение по X
     */
    private var scrolledX = 0

    /**
     * Параметр для масштабирования по оси X
     */
    private var mScaleX = _mScaleX

    /**
     * Параметр для масштабирования по оси Y
     */
    private var mScaleY = _mScaleY

    /**
     * Максимальный элемент в текущей выборке значения графика
     */
    private var maxElement = 0f

    /**
     * Минимальный элемент в текущей выборке значения графика
     */
    private var minElement = 0f

    /**
     * Список значений свечей
     */
    private val data = mutableListOf<Candle>()

    //    private val horizontalText = mutableMapOf<String,Float>()
    /**
     * Здесь хранится список всех значений из [data]
     */
    private var verticalText = mutableListOf<Float>()


    /**
     * Слушатель жестов масштабирования
     */
    private var mScaleDetector = ScaleGestureDetector(context, ScaleListener())

    /**
     * Слушатель жестов движений
     */
    private var mMoveDetector = GestureDetector(context, MoveListener())

    /**
     * Общий масштаб
     */
    private var mScaleFactor = 1.0f

    /**
     * Шаг по х при построении свечей
     */
    val stepX = dpToPx(2)

    private fun mScaleFactorX() = mScaleX / dpToPx(50)
    private fun mScaleFactorY() = mScaleY / dpToPx(50)
    private inner class MoveListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            scrollBy(distanceX.toInt(), distanceY.toInt())
            scrolledY = scrollY + height - dpToPx(20)
            scrolledX = scrollX + width - dpToPx(20)
            return true
        }

    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor
            mScaleX += detector.currentSpanX - detector.previousSpanX
            mScaleY += detector.currentSpanY - detector.previousSpanY
            mScaleX = Math.max(mMinScaleX, Math.min(mScaleX, mMaxScaleX))
            mScaleY = Math.max(mMinScaleY, Math.min(mScaleY, mMaxScaleY))
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 15.0f))
            invalidate()
            return true
        }
    }


    data class Candle(val min: Float, val max: Float, var open: Float, val close: Float) {
        fun isLow() = open > close
        fun isHigh() = open < close

        companion object {
            fun createCandle(min: Double, max: Double): Candle {
                val open = Random.nextDouble(min, max).toFloat()
                val close = Random.nextDouble(min, max).toFloat()
                val minMax = doubleArrayOf(Random.nextDouble(min, max), Random.nextDouble(min, max))
                val min = minMax.minOrNull()!!.toFloat()
                val max = minMax.maxOrNull()!!.toFloat()
                return Candle(min, max, open, close)
            }
        }
    }

    /**
     * Конвертация пикселей в DensityPixel
     */
    private fun pxToDp(px: Int) = (px / Resources.getSystem().displayMetrics.density).toInt()

    /**
     * Конвертация DensityPixel в пиксель
     */
    private fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

    fun spToPx(spValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
//    fun dip2px(dipValue: Float): Int {
//        val scale = Resources.getSystem().displayMetrics.density
//        return (dipValue * scale + 0.5f).toInt()
//    }
//
//    fun px2dip(pxValue: Float): Int {
//        val scale = Resources.getSystem().displayMetrics.density
//        return (pxValue / scale + 0.5f).toInt()
//    }

    private val TAG = "KChart"

    fun setupList() {
        var closeValue:Float? = null
        for (i in 0..20) {

            val candle = Candle.createCandle(20.0, 30.0)
            candle.open = closeValue?:candle.open
            closeValue = candle.close
            data.add(candle)
        }
        maxElement = floatArrayOf(data.maxOf { it.open },
            data.maxOf { it.close },
            data.maxOf { it.min },
            data.maxOf { it.max }).maxOrNull()!!
        minElement = floatArrayOf(data.minOf { it.open },
            data.minOf { it.close },
            data.minOf { it.min },
            data.minOf { it.max }).minOrNull()!!

        Log.d(TAG, "setupList: ${data}")
//        setupHorizontalText()
        setupVerticalList()
    }

    /**
     * Округление Float
     * @param decimals количество знаков после запятой
     */
    fun Float.round(decimals: Int): Float {
        var multiplier = 1f
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }


    private fun setupVerticalList() {
        for (c in data) {
            verticalText.add(c.close)
            verticalText.add(c.max)
            verticalText.add(c.min)
            verticalText.add(c.open)
        }
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.KChart, 0, 0).apply {
            try {
                mCandleBodySize =
                    getDimension(R.styleable.KChart_candleBodySize, dpToPx(10).toFloat())
                mCandleStringSize =
                    getDimension(R.styleable.KChart_candleStringSize, dpToPx(3).toFloat())

                mTextColor = getColor(R.styleable.KChart_mTextColor,Color.parseColor("#303030"))
                mTextSize = getDimension(R.styleable.KChart_mTextSize, spToPx(12f).toFloat())
                mMinScaleX = getDimension(R.styleable.KChart_minScaleX, dpToPx(300).toFloat())
                mMinScaleY = getDimension(R.styleable.KChart_minScaleY, dpToPx(300).toFloat())
                mMaxScaleY = getDimension(R.styleable.KChart_maxScaleY, dpToPx(2000).toFloat())
                mMaxScaleX = getDimension(R.styleable.KChart_maxScaleX, dpToPx(2000).toFloat())
                _mScaleX = getDimension(R.styleable.KChart_ScaleX, dpToPx(1300).toFloat())
                _mScaleY = getDimension(R.styleable.KChart_ScaleY, dpToPx(1300).toFloat())
                mSpaceBetweenText =
                    getDimension(R.styleable.KChart_spaceBetweenText, dpToPx(10).toFloat())
            }catch (e:Exception){
                e.printStackTrace()
            } finally {
                recycle()
            }
        }
        setupList()
    }


    /**
     * Задаём изначальные параметры для [scrolledX] и [scrolledY], которые определяют положения
     * графика и вертинкального/горизонтального текста
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        scrolledX = width - dpToPx(20)
        scrolledY = height - dpToPx(20)

        super.onLayout(changed, left, top, right, bottom)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        mMoveDetector?.onTouchEvent(event)
        return true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)





        drawLines(canvas)
//        drawHorizontalText(canvas)
        drawVerticalText(canvas)
//        drawLastCircle(canvas)

    }

    private fun Float.toScaledX() = this * mScaleFactorX()

    private fun Float.toScaledY() = this * mScaleFactorY()
    private fun Int.toScaledY() = this * mScaleFactorY()

    fun Canvas.mDrawLine(startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
        return drawLine(
            (startX).toScaledX(),
            (startY*-1+maxElement).toScaledY(),
            (stopX).toScaledX(),
            (stopY*-1+maxElement).toScaledY(),
            paint
        )
    }

    private fun drawLines(canvas: Canvas) {
        var x = 0f
        for (i in data.indices) {
            val candle = data[i]
            if (candle.isLow()) {
                canvas.mDrawLine(x, candle.max, x, candle.min, candleLowPaintSmall())
                canvas.mDrawLine(x, candle.close, x, candle.open, candleLowPaint())
            } else {

                canvas.mDrawLine(x, candle.max, x, candle.min, candleHighPaintSmall())
                canvas.mDrawLine(x, candle.close, x, candle.open, candleHighPaint())
            }
            x += stepX
        }

    }

//    private fun drawHorizontalText(canvas: Canvas) {
//        for (key in horizontalText.keys)
//                canvas.drawText(
//                    "${horizontalText[key]}",
//                    horizontalText[key]?.toScaledX() ?: continue,
//                    (scrolledY.toFloat() + 20),
//                    bottomTextPaint
//                )
//    }

    private fun coordsDiff(y1: Float, y2: Float?, lastDiff: Float = 0f): Float {
        val diff = y1.toInt().toScaledY() - (y2?.toInt() ?: 0).toScaledY()
        Log.d(
            TAG,
            "isCoordsYNear: y1=${y1} y2=${y2} diff=${abs(diff)} lastDiff=$lastDiff ${dpToPx(13)}"
        )
        return abs(diff).toFloat()
    }

    private fun isCoordsXNear(x1: Float, x2: Float?): Boolean {
        val diff = x1 - (x2 ?: 0f)
        return abs(diff) < mSpaceBetweenText

    }


    private fun drawVerticalText(canvas: Canvas) {
        val arr = verticalText.sorted().toFloatArray()
        var lastDiff = 0f
        for (i in arr.indices) {
            lastDiff += coordsDiff(arr[i], arr.elementAtOrNull(i + 1), lastDiff)

            if (lastDiff > mSpaceBetweenText) {
                canvas.drawText(
                    "${arr[i].round(2)}",
                    scrolledX + 0f,
                    (arr[i]*-1+maxElement).toScaledY(),
                    textPaint()
                )
                lastDiff = 0f
            }


        }
    }


}