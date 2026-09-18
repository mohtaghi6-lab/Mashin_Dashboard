package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import java.util.Locale

class CarPageView(
    context: Context
) : View(context) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var data =
        VehicleData.demo()

    private val background =
        Color.rgb(3, 7, 13)

    private val panel =
        Color.rgb(8, 18, 28)

    private val border =
        Color.rgb(30, 70, 90)

    private val blue =
        Color.rgb(70, 190, 255)

    private val lightBlue =
        Color.rgb(135, 225, 255)

    init {
        isFocusable = true
    }

    fun setVehicleData(
        value: VehicleData
    ) {
        data = value
        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.drawColor(background)

        drawBackgroundGlow(
            canvas,
            w,
            h
        )

        drawHeader(
            canvas,
            w
        )

        drawSpeedPanel(
            canvas,
            w * 0.25f,
            h * 0.45f,
            min(w, h) * 0.22f
        )

        drawRpmPanel(
            canvas,
            w * 0.75f,
            h * 0.45f,
            min(w, h) * 0.22f
        )

        drawInformationCards(
            canvas,
            w,
            h
        )

        drawConnectionStatus(
            canvas,
            w,
            h
        )
    }

    private fun drawBackgroundGlow(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val glowPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        glowPaint.shader =
            android.graphics.RadialGradient(
                w * 0.5f,
                h * 0.42f,
                w * 0.65f,
                intArrayOf(
                    Color.rgb(7, 35, 52),
                    Color.rgb(4, 16, 26),
                    background
                ),
                floatArrayOf(
                    0f,
                    0.5f,
                    1f
                ),
                android.graphics.Shader.TileMode.CLAMP
            )

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            glowPaint
        )
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {

        paint.style =
            Paint.Style.FILL

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textAlign =
            Paint.Align.LEFT

        paint.textSize = 28f

        paint.color =
            Color.WHITE

        canvas.drawText(
            "CAR",
            38f,
            48f,
            paint
        )

        paint.textSize = 11f

        paint.color =
            Color.rgb(
                100,
                175,
                210
            )

        canvas.drawText(
            "VEHICLE STATUS",
            40f,
            68f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.textSize = 12f

        paint.color =
            if (data.canConnected)
                lightBlue
            else
                Color.rgb(
                    145,
                    160,
                    170
                )

        canvas.drawText(
            if (data.canConnected)
                "● CAN ONLINE"
            else
                "● DEMO MODE",
            w - 38f,
            50f,
            paint
        )
    }

    private fun drawSpeedPanel(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {

        drawGauge(
            canvas,
            cx,
            cy,
            radius,
            data.speedKmh.toFloat(),
            240f,
            "km/h"
        )
    }

    private fun drawRpmPanel(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {

        drawGauge(
            canvas,
            cx,
            cy,
            radius,
            data.rpm / 1000f,
            8f,
            "x1000 RPM"
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maximum: Float,
        unit: String
    ) {

        paint.style =
            Paint.Style.STROKE

        paint.strokeCap =
            Paint.Cap.ROUND

        paint.strokeWidth =
            radius * 0.035f

        paint.color =
            Color.rgb(
                18,
                42,
                57
            )

        canvas.drawArc(
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            ),
            135f,
            270f,
            false,
            paint
        )

        paint.color =
            blue

        val progress =
            value.coerceIn(
                0f,
                maximum
            ) / maximum

        canvas.drawArc(
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            ),
            135f,
            270f * progress,
            false,
            paint
        )

        /*
         * Outer ring
         */

        paint.strokeWidth = 1.5f

        paint.color =
            Color.rgb(
                35,
                70,
                88
            )

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.82f,
            paint
        )

        /*
         * Gauge ticks
         */

        for (i in 0..24) {

            val angle =
                Math.toRadians(
                    135.0 +
                        i * 11.25
                )

            val outer =
                radius * 0.93f

            val inner =
                if (i % 3 == 0)
                    radius * 0.83f
                else
                    radius * 0.88f

            paint.strokeWidth =
                if (i % 3 == 0)
                    3f
                else
                    1.2f

            paint.color =
                if (i % 3 == 0)
                    Color.WHITE
                else
                    Color.rgb(
                        80,
                        120,
                        140
                    )

            canvas.drawLine(
                cx +
                    cos(angle).toFloat() *
                    inner,
                cy +
                    sin(angle).toFloat() *
                    inner,
                cx +
                    cos(angle).toFloat() *
                    outer,
                cy +
                    sin(angle).toFloat() *
                    outer,
                paint
            )
        }

        /*
         * Needle
         */

        val needleAngle =
            Math.toRadians(
                135.0 +
                    270.0 * progress
            )

        paint.strokeWidth =
            3.5f

        paint.color =
            Color.WHITE

        canvas.drawLine(
            cx,
            cy,
            cx +
                cos(needleAngle).toFloat() *
                radius * 0.70f,
            cy +
                sin(needleAngle).toFloat() *
                radius * 0.70f,
            paint
        )

        /*
         * Center
         */

        paint.style =
            Paint.Style.FILL

        paint.color =
            blue

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.045f,
            paint
        )

        /*
         * Value
         */

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            radius * 0.29f

        paint.color =
            Color.WHITE

        val valueText =
            if (maximum > 10f) {
                value.toInt().toString()
            } else {
                String.format(
                    Locale.US,
                    "%.1f",
                    value
                )
            }

        canvas.drawText(
            valueText,
            cx,
            cy + radius * 0.10f,
            paint
        )

        /*
         * Unit
         */

        paint.textSize =
            radius * 0.075f

        paint.color =
            lightBlue

        canvas.drawText(
            unit,
            cx,
            cy + radius * 0.27f,
            paint
        )
    }

    private fun drawInformationCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val top =
            h * 0.70f

        val cardHeight =
            76f

        val gap =
            14f

        val margin =
            35f

        val totalWidth =
            w - margin * 2f

        val cardWidth =
            (totalWidth - gap * 3f) / 4f

        val titles =
            arrayOf(
                "ENGINE",
                "BATTERY",
                "FUEL",
                "ODOMETER"
            )

        val values =
            arrayOf(
                "${data.engineTempC} °C",
                String.format(
                    Locale.US,
                    "%.1f V",
                    data.batteryVoltage
                ),
                "${data.fuelPercent} %",
                "${data.odometerKm} km"
            )

        for (i in 0..3) {

            val left =
                margin +
                    i * (cardWidth + gap)

            val right =
                left + cardWidth

            paint.style =
                Paint.Style.FILL

            paint.color =
                panel

            canvas.drawRoundRect(
                left,
                top,
                right,
                top + cardHeight,
                18f,
                18f,
                paint
            )

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f

            paint.color =
                border

            canvas.drawRoundRect(
                left,
                top,
                right,
                top + cardHeight,
                18f,
                18f,
                paint
            )

            paint.style =
                Paint.Style.FILL

            paint.textAlign =
                Paint.Align.CENTER

            paint.typeface =
                Typeface.DEFAULT

            paint.textSize =
                10f

            paint.color =
                Color.rgb(
                    105,
                    150,
                    175
                )

            canvas.drawText(
                titles[i],
                (left + right) / 2f,
                top + 23f,
                paint
            )

            paint.typeface =
                Typeface.DEFAULT_BOLD

            paint.textSize =
                18f

            paint.color =
                Color.WHITE

            canvas.drawText(
                values[i],
                (left + right) / 2f,
                top + 51f,
                paint
            )
        }
    }

    private fun drawConnectionStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.LEFT

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                80,
                125,
                150
            )

        canvas.drawText(
            if (data.obdConnected)
                "OBD CONNECTED"
            else
                "OBD STANDBY",
            35f,
            h - 24f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.color =
            if (data.ecuErrorCount > 0)
                Color.rgb(
                    230,
                    90,
                    90
                )
            else
                Color.rgb(
                    100,
                    190,
                    150
                )

        canvas.drawText(
            if (data.ecuErrorCount > 0)
                "${data.ecuErrorCount} ECU ERRORS"
            else
                "NO ECU ERRORS",
            w - 35f,
            h - 24f,
            paint
        )
    }
}
