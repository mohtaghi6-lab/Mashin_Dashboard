package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

import peugeot.platform.android.vehicle.VehicleData


class MusicPageView(
    context: Context
) : View(context) {


    var onBackClick: (() -> Unit)? = null
    var onPlayPauseClick: (() -> Unit)? = null
    var onPreviousClick: (() -> Unit)? = null
    var onNextClick: (() -> Unit)? = null


    private val backgroundPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val panelPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val strokePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val textPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var widthPx = 0f
    private var heightPx = 0f


    private var isPlaying = false

    private var playbackProgress = 0.42f


    private var vehicleData =
        VehicleData.demo()


    private val albumRect = RectF()
    private val playRect = RectF()
    private val previousRect = RectF()
    private val nextRect = RectF()
    private val backRect = RectF()



    init {

        isClickable = true
        isFocusable = true


        backgroundPaint.shader =
            LinearGradient(
                0f,
                0f,
                0f,
                1400f,
                Color.rgb(3,7,15),
                Color.rgb(8,19,34),
                Shader.TileMode.CLAMP
            )


        panelPaint.color =
            Color.argb(
                125,
                15,
                27,
                43
            )


        strokePaint.style =
            Paint.Style.STROKE

        strokePaint.strokeCap =
            Paint.Cap.ROUND
    }



    fun setVehicleData(
        data: VehicleData
    ) {

        vehicleData = data

        invalidate()
    }



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        widthPx =
            width.toFloat()

        heightPx =
            height.toFloat()



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
    private fun drawAmbientGlow(
    canvas: Canvas
) {

    val glow =
        Paint(Paint.ANTI_ALIAS_FLAG)

    glow.shader =
        RadialGradient(
            widthPx * 0.50f,
            heightPx * 0.38f,
            widthPx * 0.55f,
            intArrayOf(
                Color.argb(70,40,130,255),
                Color.argb(22,40,100,190),
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



private fun drawHeader(
    canvas: Canvas
) {

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


    textPaint.typeface =
        Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

    textPaint.color =
        Color.WHITE

    textPaint.textSize =
        25f


    canvas.drawText(
        "MUSIC",
        left + 28f,
        top + 38f,
        textPaint
    )


    textPaint.typeface =
        Typeface.DEFAULT

    textPaint.color =
        Color.rgb(
            105,
            180,
            255
        )

    textPaint.textSize =
        15f


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


    backRect.set(
        left + 16f,
        top + 12f,
        left + 72f,
        bottom - 10f
    )


    textPaint.color =
        Color.WHITE

    textPaint.textSize =
        28f


    canvas.drawText(
        "‹",
        left + 31f,
        top + 47f,
        textPaint
    )
}



private fun drawBluetoothStatus(
    canvas: Canvas,
    x: Float,
    y: Float
) {

    val p =
        Paint(Paint.ANTI_ALIAS_FLAG)


    p.style =
        Paint.Style.STROKE

    p.strokeWidth =
        2.5f

    p.color =
        Color.rgb(
            70,
            180,
            255
        )


    val path =
        Path()


    path.moveTo(
        x,
        y - 10f
    )

    path.lineTo(
        x,
        y + 10f
    )

    path.lineTo(
        x + 7f,
        y + 4f
    )

    path.lineTo(
        x - 6f,
        y - 7f
    )

    path.lineTo(
        x + 7f,
        y - 14f
    )

    path.lineTo(
        x + 7f,
        y + 14f
    )

    path.lineTo(
        x - 6f,
        y + 7f
    )


    canvas.drawPath(
        path,
        p
    )


    textPaint.color =
        Color.WHITE

    textPaint.textSize =
        13f


    canvas.drawText(
        "BLUETOOTH CONNECTED",
        x + 20f,
        y + 5f,
        textPaint
    )
}



private fun drawAlbum(
    canvas: Canvas
) {

    val size =
        min(
            widthPx * 0.44f,
            420f
        )


    val left =
        (widthPx - size) * 0.5f


    val top =
        140f


    albumRect.set(
        left,
        top,
        left + size,
        top + size
    )


    val shadow =
        Paint(Paint.ANTI_ALIAS_FLAG)


    shadow.color =
        Color.argb(
            90,
            0,
            0,
            0
        )


    canvas.drawRoundRect(
        albumRect.left + 8f,
        albumRect.top + 10f,
        albumRect.right + 8f,
        albumRect.bottom + 10f,
        36f,
        36f,
        shadow
    )


    val albumPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    albumPaint.shader =
        LinearGradient(
            albumRect.left,
            albumRect.top,
            albumRect.right,
            albumRect.bottom,
            Color.rgb(19,49,83),
            Color.rgb(5,13,25),
            Shader.TileMode.CLAMP
        )


    canvas.drawRoundRect(
        albumRect,
        36f,
        36f,
        albumPaint
    )


    strokePaint.color =
        Color.argb(
            160,
            80,
            170,
            255
        )

    strokePaint.strokeWidth =
        2f


    canvas.drawRoundRect(
        albumRect,
        36f,
        36f,
        strokePaint
    )


    val cx =
        albumRect.centerX()

    val cy =
        albumRect.centerY()


    val ring =
        Paint(Paint.ANTI_ALIAS_FLAG)


    ring.style =
        Paint.Style.STROKE

    ring.strokeWidth =
        10f

    ring.color =
        Color.argb(
            110,
            75,
            165,
            255
        )


    canvas.drawCircle(
        cx,
        cy,
        size * 0.27f,
        ring
    )


    ring.strokeWidth =
        2f

    ring.color =
        Color.argb(
            220,
            150,
            210,
            255
        )


    canvas.drawCircle(
        cx,
        cy,
        size * 0.34f,
        ring
    )


    textPaint.textAlign =
        Paint.Align.CENTER

    textPaint.color =
        Color.WHITE

    textPaint.typeface =
        Typeface.DEFAULT_BOLD

    textPaint.textSize =
        size * 0.22f


    canvas.drawText(
        "♪",
        cx,
        cy + size * 0.075f,
        textPaint
    )


    textPaint.textSize =
        13f

    textPaint.color =
        Color.argb(
            180,
            180,
            220,
            255
        )


    canvas.drawText(
        "CONNECTED MEDIA",
        cx,
        albumRect.bottom - 28f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.LEFT
}
private fun drawTrackInfo(
    canvas: Canvas
) {

    val centerX =
        widthPx * 0.5f

    val y =
        albumRect.bottom + 54f


    textPaint.textAlign =
        Paint.Align.CENTER

    textPaint.typeface =
        Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

    textPaint.color =
        Color.WHITE

    textPaint.textSize =
        27f


    canvas.drawText(
        "BMW LUXURY AUDIO",
        centerX,
        y,
        textPaint
    )


    textPaint.typeface =
        Typeface.DEFAULT

    textPaint.textSize =
        15f

    textPaint.color =
        Color.rgb(
            105,
            180,
            255
        )


    canvas.drawText(
        "PEUGEOT PARS • BLUETOOTH",
        centerX,
        y + 28f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.LEFT
}



private fun drawProgress(
    canvas: Canvas
) {

    val left = 72f
    val right = widthPx - 72f
    val y = albumRect.bottom + 108f


    val trackPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    trackPaint.strokeWidth =
        6f

    trackPaint.strokeCap =
        Paint.Cap.ROUND


    trackPaint.color =
        Color.argb(
            70,
            150,
            190,
            230
        )


    canvas.drawLine(
        left,
        y,
        right,
        y,
        trackPaint
    )


    trackPaint.color =
        Color.rgb(
            75,
            170,
            255
        )


    canvas.drawLine(
        left,
        y,
        left + (right - left) * playbackProgress,
        y,
        trackPaint
    )


    val knobPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    knobPaint.color =
        Color.WHITE


    canvas.drawCircle(
        left + (right - left) * playbackProgress,
        y,
        8f,
        knobPaint
    )


    textPaint.color =
        Color.argb(
            180,
            210,
            225,
            245
        )

    textPaint.textSize =
        12f


    canvas.drawText(
        "02:18",
        left,
        y + 25f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.RIGHT


    canvas.drawText(
        "05:32",
        right,
        y + 25f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.LEFT
}



private fun drawControls(
    canvas: Canvas
) {

    val centerY =
        albumRect.bottom + 175f

    val centerX =
        widthPx * 0.5f


    previousRect.set(
        centerX - 145f,
        centerY - 38f,
        centerX - 70f,
        centerY + 38f
    )


    playRect.set(
        centerX - 40f,
        centerY - 48f,
        centerX + 40f,
        centerY + 48f
    )


    nextRect.set(
        centerX + 70f,
        centerY - 38f,
        centerX + 145f,
        centerY + 38f
    )


    drawControlCircle(
        canvas,
        previousRect,
        false,
        "‹"
    )


    drawControlCircle(
        canvas,
        playRect,
        true,
        if (isPlaying) "Ⅱ" else "▶"
    )


    drawControlCircle(
        canvas,
        nextRect,
        false,
        "›"
    )
}



private fun drawControlCircle(
    canvas: Canvas,
    rect: RectF,
    primary: Boolean,
    symbol: String
) {

    val p =
        Paint(Paint.ANTI_ALIAS_FLAG)


    p.color =
        if (primary)
            Color.argb(220,48,125,210)
        else
            Color.argb(115,20,40,62)


    canvas.drawCircle(
        rect.centerX(),
        rect.centerY(),
        rect.width() * 0.5f,
        p
    )


    strokePaint.color =
        Color.argb(
            180,
            100,
            190,
            255
        )


    canvas.drawCircle(
        rect.centerX(),
        rect.centerY(),
        rect.width() * 0.5f,
        strokePaint
    )


    textPaint.textAlign =
        Paint.Align.CENTER

    textPaint.color =
        Color.WHITE

    textPaint.textSize =
        if (primary) 25f else 32f


    canvas.drawText(
        symbol,
        rect.centerX(),
        rect.centerY() + 9f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.LEFT
}



private fun drawConnectionPanel(
    canvas: Canvas
) {

    drawGlassPanel(
        canvas,
        40f,
        heightPx - 178f,
        widthPx - 40f,
        heightPx - 88f,
        24f
    )


    textPaint.color =
        Color.WHITE

    textPaint.textSize =
        14f

    textPaint.typeface =
        Typeface.DEFAULT_BOLD


    canvas.drawText(
        "AUDIO LINK",
        64f,
        heightPx - 148f,
        textPaint
    )


    textPaint.typeface =
        Typeface.DEFAULT

    textPaint.color =
        Color.rgb(
            100,
            205,
            255
        )


    canvas.drawText(
        "BLUETOOTH • READY",
        64f,
        heightPx - 122f,
        textPaint
    )


    val dot =
        Paint(Paint.ANTI_ALIAS_FLAG)

    dot.color =
        Color.rgb(
            65,
            220,
            145
        )


    canvas.drawCircle(
        widthPx - 68f,
        heightPx - 135f,
        6f,
        dot
    )
}



private fun drawBottomBar(
    canvas: Canvas
) {

    drawGlassPanel(
        canvas,
        28f,
        heightPx - 72f,
        widthPx - 28f,
        heightPx - 20f,
        20f
    )


    textPaint.color =
        Color.argb(
            210,
            190,
            215,
            240
        )

    textPaint.textSize =
        12f


    canvas.drawText(
        "‹ BACK TO BMW HOME",
        50f,
        heightPx - 40f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.RIGHT


    textPaint.color =
        Color.rgb(
            85,
            175,
            255
        )


    canvas.drawText(
        if (isPlaying)
            "PLAYING"
        else
            "PAUSED",
        widthPx - 50f,
        heightPx - 40f,
        textPaint
    )


    textPaint.textAlign =
        Paint.Align.LEFT
}



private fun drawGlassPanel(
    canvas: Canvas,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    radius: Float
) {

    val fill =
        Paint(Paint.ANTI_ALIAS_FLAG)

    fill.color =
        Color.argb(
            115,
            18,
            31,
            48
        )


    canvas.drawRoundRect(
        left,
        top,
        right,
        bottom,
        radius,
        radius,
        fill
    )


    val border =
        Paint(Paint.ANTI_ALIAS_FLAG)


    border.style =
        Paint.Style.STROKE

    border.strokeWidth =
        1.5f

    border.color =
        Color.argb(
            100,
            100,
            180,
            255
        )


    canvas.drawRoundRect(
        left,
        top,
        right,
        bottom,
        radius,
        radius,
        border
    )
}



override fun onTouchEvent(
    event: MotionEvent
): Boolean {


    if (
        event.action != MotionEvent.ACTION_UP
    ) {
        return true
    }


    val x =
        event.x

    val y =
        event.y



    when {


        backRect.contains(x,y) -> {
            onBackClick?.invoke()
        }


        previousRect.contains(x,y) -> {
            onPreviousClick?.invoke()
        }


        nextRect.contains(x,y) -> {
            onNextClick?.invoke()
        }


        playRect.contains(x,y) -> {

            isPlaying =
                !isPlaying

            onPlayPauseClick?.invoke()

            invalidate()
        }


        y >= heightPx - 92f -> {

            onBackClick?.invoke()
        }


        y >= albumRect.bottom + 80f &&
        y <= albumRect.bottom + 140f -> {


            playbackProgress =
                ((x - 72f) /
                (widthPx - 144f))
                .coerceIn(0f,1f)


            invalidate()
        }
    }


    return true
}

}
