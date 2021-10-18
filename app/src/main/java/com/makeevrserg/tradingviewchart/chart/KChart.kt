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
import com.makeevrserg.tradingviewchart.network.response.stocks.Data
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.round
import kotlin.random.Random

class KChart(context: Context, val attrs: AttributeSet?) : View(context, attrs) {


    var mCandleBodySize: Float = dpToPx(10).toFloat()
    var mCandleStringSize: Float = dpToPx(3).toFloat()
    var mTextSize: Float = spToPx(12f).toFloat()
    var mTextColor: Int = Color.parseColor("#303030")
    var mMinScaleX: Float = dpToPx(300).toFloat()
    var mMinScaleY: Float = dpToPx(100).toFloat()
    var mMaxScaleY: Float = dpToPx(2000).toFloat()
    var mMaxScaleX: Float = dpToPx(2000).toFloat()
    var mScaleX: Float = dpToPx(1300).toFloat()
    var mScaleY: Float = dpToPx(600).toFloat()
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
//    private var mScaleX = this.mScaleX

    /**
     * Параметр для масштабирования по оси Y
     */
//    private var mScaleY = this.mScaleY

    /**
     * Максимальный элемент в текущей выборке значения графика
     */
    private var maxElement = 0.0

    /**
     * Минимальный элемент в текущей выборке значения графика
     */
    private var minElement = 0.0

    /**
     * Список значений свечей
     */
    private var data = listOf<Data>()

    private val horizontalText = mutableListOf<String>()

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

    private fun mScaleFactorX() = this.mScaleX / dpToPx(50)
    private fun mScaleFactorY() = this.mScaleY / dpToPx(50)
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
            this@KChart.mScaleX += detector.currentSpanX - detector.previousSpanX
            this@KChart.mScaleY += detector.currentSpanY - detector.previousSpanY
            this@KChart.mScaleX = Math.max(mMinScaleX, Math.min(this@KChart.mScaleX, mMaxScaleX))
            this@KChart.mScaleY = Math.max(mMinScaleY, Math.min(this@KChart.mScaleY, mMaxScaleY))
            mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 15.0f))
            invalidate()
            return true
        }
    }


    data class Candle(val low: Float, val high: Float, var open: Float, val close: Float) {
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

//    fun setupList() {
//        var closeValue:Float? = null
//        for (i in 0..20) {
//
//            val candle = Candle.createCandle(20.0, 30.0)
//            candle.open = closeValue?:candle.open
//            closeValue = candle.close
//            data.add(candle)
//        }
//        maxElement = floatArrayOf(data.maxOf { it.open },
//            data.maxOf { it.close },
//            data.maxOf { it.low },
//            data.maxOf { it.high }).maxOrNull()!!
//        minElement = floatArrayOf(data.minOf { it.open },
//            data.minOf { it.close },
//            data.minOf { it.low },
//            data.minOf { it.high }).minOrNull()!!
//
//        Log.d(TAG, "setupList: ${data}")
////        setupHorizontalText()
//        setupVerticalList()
//    }

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
            verticalText.add(c.close.toFloat())
            verticalText.add(c.high.toFloat())
            verticalText.add(c.low.toFloat())
            verticalText.add(c.open.toFloat())
        }
    }

    private fun setupHorizontalList() {
        for (c in data)
            horizontalText.add(c.date)

    }

    fun update(it: List<Data>?) {
        data = it?.filter { iit -> iit.volume != 0 }?.sortedBy { iit->iit.date} ?: return
        minElement = arrayOf(data.minOf { it.close },
            data.minOf { it.high },
            data.minOf { it.low },
            data.minOf { it.open }).minOrNull()!!
        maxElement = arrayOf(data.maxOf { it.close },
            data.maxOf { it.high },
            data.maxOf { it.low },
            data.maxOf { it.open }).maxOrNull()!!
        Log.d(TAG, "update: data=${data.toList()}")
        setupVerticalList()
        setupHorizontalList()
        invalidate()
    }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.KChart, 0, 0).apply {
            try {
                mCandleBodySize =
                    getDimension(R.styleable.KChart_candleBodySize, dpToPx(10).toFloat())
                mCandleStringSize =
                    getDimension(R.styleable.KChart_candleStringSize, dpToPx(3).toFloat())

                mTextColor =
                    getColor(R.styleable.KChart_mTextColor, Color.parseColor("#303030"))
                mTextSize = getDimension(R.styleable.KChart_mTextSize, spToPx(12f).toFloat())
                mMinScaleX = getDimension(R.styleable.KChart_minScaleX, dpToPx(300).toFloat())
                mMinScaleY = getDimension(R.styleable.KChart_minScaleY, dpToPx(300).toFloat())
                mMaxScaleY = getDimension(R.styleable.KChart_maxScaleY, dpToPx(2000).toFloat())
                mMaxScaleX = getDimension(R.styleable.KChart_maxScaleX, dpToPx(2000).toFloat())
                this@KChart.mScaleX = getDimension(R.styleable.KChart_ScaleX, dpToPx(1300).toFloat())
                this@KChart.mScaleY = getDimension(R.styleable.KChart_ScaleY, dpToPx(600).toFloat())
                mSpaceBetweenText =
                    getDimension(R.styleable.KChart_spaceBetweenText, dpToPx(10).toFloat())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                recycle()
            }
        }
//        setupList()
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
        drawHorizontalText(canvas)
        drawVerticalText(canvas)
//        drawLastCircle(canvas)

    }

    private fun Float.toScaledX() = this * mScaleFactorX()

    private fun Float.toScaledY() = this * mScaleFactorY()
    private fun Int.toScaledY() = this * mScaleFactorY()

    fun Canvas.mDrawLine(
        startX: Float,
        startY: Float,
        stopX: Float,
        stopY: Float,
        paint: Paint
    ) {
        return drawLine(
            (startX).toScaledX(),
            (startY * -1 + maxElement).toFloat().toScaledY(),
            (stopX).toScaledX(),
            (stopY * -1 + maxElement).toFloat().toScaledY(),
            paint
        )
    }

    private fun drawLines(canvas: Canvas) {
        var x = 0f
        for (i in data.indices) {
            val candle = data[i]
            if (candle.isLow()) {
                canvas.mDrawLine(
                    x,
                    candle.high.toFloat(),
                    x,
                    candle.low.toFloat(),
                    candleLowPaintSmall()
                )
                canvas.mDrawLine(
                    x,
                    candle.close.toFloat(),
                    x,
                    candle.open.toFloat(),
                    candleLowPaint()
                )
            } else {

                canvas.mDrawLine(
                    x,
                    candle.high.toFloat(),
                    x,
                    candle.low.toFloat(),
                    candleHighPaintSmall()
                )
                canvas.mDrawLine(
                    x,
                    candle.close.toFloat(),
                    x,
                    candle.open.toFloat(),
                    candleHighPaint()
                )
            }
            x += stepX
        }

    }

    fun String.toFormat(format:String="HH:mm:ss"): String {
        val inputFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormat = SimpleDateFormat(format)
        val d: Date = inputFormat.parse(this)
        val formattedDate: String = outputFormat.format(d)
        return formattedDate
    }
    private fun drawHorizontalText(canvas: Canvas) {
        var i = 0
        var lastDiff = 0f
        for (dat in data) {

            lastDiff += coordsDiff(i, i + stepX, lastDiff)
            if (lastDiff > mSpaceBetweenText+dpToPx(20)) {
                canvas.drawText(
                    "${dat.date.toFormat("HH:mm")}",
                    (i).toFloat().toScaledX(),
                    (scrolledY.toFloat() + dpToPx(10)),
                    textPaint()
                )


                canvas.drawText(
                    "${dat.date.toFormat("MM-dd")}",
                    (i).toFloat().toScaledX(),
                    (scrolledY.toFloat() - dpToPx(10)),
                    textPaint()
                )
            }
            i+=stepX
        }
    }

    private fun coordsDiff(x1: Int, x2: Int, lastDiff: Float = 0f): Float {
        val diff = x1.toFloat().toScaledX() - x2.toFloat().toScaledX()
        return abs(diff).toFloat()
    }

    private fun coordsDiff(y1: Float, y2: Float?, lastDiff: Float = 0f): Float {
        val diff = y1.toInt().toScaledY() - (y2?.toInt() ?: 0).toScaledY()
        return abs(diff).toFloat()
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
                    (arr[i] * -1 + maxElement).toFloat().toScaledY(),
                    textPaint()
                )
                lastDiff = 0f
            }


        }
    }


}