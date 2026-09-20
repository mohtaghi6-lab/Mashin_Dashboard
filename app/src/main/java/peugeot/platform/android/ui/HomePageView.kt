package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class HomePageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var aiState =
        AIState.IDLE


    private var pulse = 0f


    var onAIOrbClick: (() -> Unit)? = null


    private val blue =
        Color.rgb(40, 170, 255)


    private val cyan =
        Color.rgb(100, 230, 255)


    private val white =
        Color.WHITE


    private val muted =
        Color.rgb(150, 170, 190)


    private val glass =
        Color.argb(
            150,
            8,
            20,
            35
        )


    private val timeFormat =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )


    private val dateFormat =
        SimpleDateFormat(
            "EEE, dd MMM",
            Locale.ENGLISH
        )


    fun setVehicleData(
        data: VehicleData
    ) {
        vehicleData = data
        invalidate()
    }


    fun setAIState(
        state: AIState
    ) {
        aiState = state
        invalidate()
    }


    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)


        val w =
            width.toFloat()


        val h =
            height.toFloat()


        if (w <= 0f || h <= 0f)
            return



        drawBackground(
            canvas,
            w,
            h
        )


        drawHeader(
            canvas,
            w
        )


        val radius =
            min(w, h) * 0.18f


        val centerY =
            h * 0.48f



        drawGauge(
            canvas,
            w * 0.22f,
            centerY,
            radius,
            vehicleData.speedKmh.toFloat(),
            240f,
            "km/h",
            "SPEED"
        )


        drawGauge(
            canvas,
            w * 0.78f,
            centerY,
            radius,
            vehicleData.rpm / 1000f,
            8f,
            "x1000 RPM",
            "ENGINE"
        )



        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.43f,
            min(w, h) * 0.10f
        )



        drawCenterInfo(
            canvas,
            w,
            h
        )


        drawStatusCards(
            canvas,
            w,
            h
        )


        pulse += 0.035f


        postInvalidateOnAnimation()
    }
    private fun drawBackground(
    canvas: Canvas,
    w: Float,
    h: Float
) {

    canvas.drawColor(
        Color.rgb(1, 4, 9)
    )


    val glow =
        Paint(Paint.ANTI_ALIAS_FLAG)


    glow.shader =
        RadialGradient(
            w * 0.5f,
            h * 0.35f,
            min(w, h) * 0.75f,
            intArrayOf(
                Color.rgb(10, 70, 110),
                Color.rgb(5, 25, 45),
                Color.rgb(1, 4, 9)
            ),
            floatArrayOf(
                0f,
                0.45f,
                1f
            ),
            Shader.TileMode.CLAMP
        )


    canvas.drawRect(
        0f,
        0f,
        w,
        h,
        glow
    )


    val glassPaint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    glassPaint.shader =
        LinearGradient(
            0f,
            h * 0.55f,
            0f,
            h,
            Color.argb(
                0,
                80,
                200,
                255
            ),
            Color.argb(
                80,
                20,
                100,
                170
            ),
            Shader.TileMode.CLAMP
        )


    canvas.drawRect(
        0f,
        h * 0.55f,
        w,
        h,
        glassPaint
    )
}



private fun drawHeader(
    canvas: Canvas,
    w: Float
) {

    paint.style =
        Paint.Style.FILL


    paint.textAlign =
        Paint.Align.LEFT


    paint.typeface =
        Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )


    paint.textSize =
        20f


    paint.color =
        white


    canvas.drawText(
        "MRT",
        35f,
        40f,
        paint
    )


    paint.typeface =
        Typeface.DEFAULT


    paint.textSize =
        10f


    paint.color =
        muted


    canvas.drawText(
        "LUXURY VEHICLE OS",
        35f,
        57f,
        paint
    )



    paint.textAlign =
        Paint.Align.CENTER


    paint.typeface =
        Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )


    paint.textSize =
        36f


    paint.color =
        white


    canvas.drawText(
        timeFormat.format(Date()),
        w / 2f,
        42f,
        paint
    )



    paint.textSize =
        10f


    paint.typeface =
        Typeface.DEFAULT


    paint.color =
        cyan


    canvas.drawText(
        dateFormat.format(Date()).uppercase(),
        w / 2f,
        60f,
        paint
    )



    paint.textAlign =
        Paint.Align.RIGHT


    paint.textSize =
        11f


    paint.color =
        cyan


    canvas.drawText(
        if (vehicleData.canConnected)
            "CAN ONLINE"
        else
            "CAN STANDBY",
        w - 35f,
        40f,
        paint
    )


    paint.color =
        muted


    canvas.drawText(
        "${vehicleData.batteryVoltage}V",
        w - 35f,
        57f,
        paint
    )
}



private fun drawGauge(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    radius: Float,
    value: Float,
    max: Float,
    unit: String,
    title: String
) {

    val progress =
        value.coerceIn(0f, max) / max



    val rect =
        RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )



    paint.style =
        Paint.Style.STROKE


    paint.strokeCap =
        Paint.Cap.ROUND


    paint.strokeWidth =
        radius * 0.06f



    paint.color =
        Color.rgb(
            20,
            45,
            65
        )


    canvas.drawArc(
        rect,
        140f,
        260f,
        false,
        paint
    )



    paint.color =
        blue


    canvas.drawArc(
        rect,
        140f,
        260f * progress,
        false,
        paint
    )



    val angle =
        Math.toRadians(
            140.0 +
                    260.0 * progress
        )



    paint.strokeWidth =
        4f


    paint.color =
        white


    canvas.drawLine(
        cx,
        cy,
        cx + cos(angle).toFloat()
                * radius * 0.7f,
        cy + sin(angle).toFloat()
                * radius * 0.7f,
        paint
    )



    paint.style =
        Paint.Style.FILL


    paint.color =
        cyan


    canvas.drawCircle(
        cx,
        cy,
        radius * 0.05f,
        paint
    )


    paint.textAlign =
        Paint.Align.CENTER


    paint.textSize =
        radius * 0.25f


    paint.typeface =
        Typeface.DEFAULT_BOLD


    paint.color =
        white


    canvas.drawText(
        if (max > 10)
            value.toInt().toString()
        else
            String.format(
                Locale.US,
                "%.1f",
                value
            ),
        cx,
        cy + 10f,
        paint
    )


    paint.textSize =
        12f


    paint.color =
        cyan


    canvas.drawText(
        unit,
        cx,
        cy + 35f,
        paint
    )


    paint.textSize =
        10f


    paint.color =
        muted


    canvas.drawText(
        title,
        cx,
        cy + radius + 25f,
        paint
    )
}
    private fun drawBottomCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val top = h * 0.72f
        val bottom = h * 0.84f

        val margin = 35f
        val gap = 14f

        val cardWidth =
            (w - margin * 2 - gap * 2) / 3f


        drawGlassCard(
            canvas,
            margin,
            top,
            margin + cardWidth,
            bottom,
            "ENGINE",
            vehicleData.engineTempC.toString() + "°C"
        )


        drawGlassCard(
            canvas,
            margin + cardWidth + gap,
            top,
            margin + cardWidth * 2 + gap,
            bottom,
            "FUEL",
            vehicleData.fuelPercent.toString() + "%"
        )


        drawGlassCard(
            canvas,
            margin + cardWidth * 2 + gap * 2,
            top,
            w - margin,
            bottom,
            "CAN",
            if (vehicleData.canConnected)
                "ONLINE"
            else
                "STANDBY"
        )
    }



    private fun drawGlassCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        value: String
    ) {

        paint.style = Paint.Style.FILL

        paint.color =
            Color.argb(
                150,
                10,
                25,
                40
            )


        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            22f,
            22f,
            paint
        )


        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            1.5f

        paint.color =
            Color.argb(
                130,
                80,
                200,
                255
            )


        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            22f,
            22f,
            paint
        )


        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            10f

        paint.color =
            gray


        canvas.drawText(
            title,
            (left + right) / 2f,
            top + 26f,
            paint
        )


        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            18f

        paint.color =
            white


        canvas.drawText(
            value,
            (left + right) / 2f,
            top + 55f,
            paint
        )
    }



    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if (
            event.action ==
            MotionEvent.ACTION_UP
        ) {

            val dx =
                event.x - width / 2f

            val dy =
                event.y - height * 0.42f


            val distance =
                kotlin.math.sqrt(
                    dx * dx +
                    dy * dy
                )


            if (
                distance <
                min(width, height) * 0.14f
            ) {

                onAIOrbClick?.invoke()

                return true
            }
        }


        return true
    }

}
