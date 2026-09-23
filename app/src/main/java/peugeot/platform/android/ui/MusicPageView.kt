package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class MusicPageView(
    context: Context
) : View(context) {

    var onBackClick: (() -> Unit)? = null
    var onPlayPauseClick: (() -> Unit)? = null
    var onPreviousClick: (() -> Unit)? = null
    var onNextClick: (() -> Unit)? = null

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var widthPx = 0f
    private var heightPx = 0f

    private var isPlaying = false
    private var progress = 0.42f

    private val albumRect = RectF()
    private val playRect = RectF()
    private val previousRect = RectF()
    private val nextRect = RectF()
    private val backRect = RectF()

    init {
        isClickable = true
        isFocusable = true

        backgroundPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            1400f,
            Color.rgb(3, 7, 15),
            Color.rgb(8, 19, 34),
            Shader.TileMode.CLAMP
        )

        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        widthPx = width.toFloat()
        heightPx = height.toFloat()

        canvas.drawRect(
            0f,
            0f,
            widthPx,
            heightPx,
            backgroundPaint
        )

        drawAmbientGlow(canvas)
        drawHeader(canvas)
        drawAlbum(canvas)
        drawTrackInfo(canvas)
        drawProgress(canvas)
        drawControls(canvas)
        drawConnectionPanel(canvas)
        drawBottomBar(canvas)
    }

    private fun drawAmbientGlow(canvas: Canvas) {
        val glow = Paint(Paint.ANTI_ALIAS_FLAG)

        glow.shader = RadialGradient(
            widthPx * 0.50f,
            heightPx * 0.38f,
            widthPx * 0.55f,
            intArrayOf(
                Color.argb(70, 40, 130, 255),
                Color.argb(22, 40, 100, 190),
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            widthPx * 0.50f,
            heightPx * 0.38f,
            widthPx * 0.55f,
            glow
        )
    }

    private fun drawHeader(canvas: Canvas) {
        val left = 28f
        val top = 24f
        val right = widthPx - 28f
        val bottom = 108f

        drawGlassPanel(
            canvas,
            left,
            top,
            right,
            bottom,
            28f
        )

        textPaint.typeface = Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

        textPaint.color = Color.WHITE
        textPaint.textSize = 25f

        canvas.drawText(
            "MUSIC",
            left + 28f,
            top + 38f,
            textPaint
        )

        textPaint.typeface = Typeface.DEFAULT
        textPaint.color = Color.rgb(105, 180, 255)
        textPaint.textSize = 15f

        canvas.drawText(
            "BMW LUXURY MEDIA",
            left + 29f,
            top + 64f,
            textPaint
        )

        drawBluetoothStatus(
            canvas,
            right - 155f,
            top + 43f
        )
    }

    private fun drawBluetoothStatus(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.rgb(
            70,
            180,
            255
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f

        val path = Path()

        path.moveTo(x, y - 10f)
        path.lineTo(x, y + 10f)
        path.lineTo(x + 7f, y + 4f)
        path.lineTo(x - 6f, y - 7f)
        path.lineTo(x + 7f, y - 14f)
        path.lineTo(x + 7f, y + 14f)
        path.lineTo(x - 6f, y + 7f)

        canvas.drawPath(
            path,
            paint
        )

        textPaint.color = Color.WHITE
        textPaint.textSize = 13f
        textPaint.typeface = Typeface.DEFAULT

        canvas.drawText(
            "BLUETOOTH CONNECTED",
            x + 20f,
            y + 5f,
            textPaint
        )
    }

    private fun drawAlbum(canvas: Canvas) {
        val size = min

