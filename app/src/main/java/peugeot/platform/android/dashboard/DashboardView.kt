package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.View
import peugeot.platform.android.vehicle.VehicleData
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DashboardView(context: Context) : View(context) {

    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private var data = VehicleData.demo()

    fun setVehicleData(value: VehicleData) {
        data = value
        invalidate()
    }

    override fun onDraw(c: Canvas) {

        val w = width.toFloat()
        val h = height.toFloat()

        c.drawColor(Color.rgb(4, 8, 14))

        p.textAlign = Paint.Align.CENTER

        val cy = h * 0.48f
        val r = min(w * 0.28f, h * 0.36f)

        gauge(
            c,
            w * 0.27f,
            cy,
            r,
            data.speedKmh.toFloat(),
            240f
        )

        gauge(
            c,
            w * 0.73f,
            cy,
            r,
            data.rpm / 1000f,
            8f
        )

        p.style = Paint.Style.FILL
        p.typeface = Typeface.DEFAULT_BOLD
        p.color = Color.WHITE
        p.textSize = r * 0.43f

        c.drawText(
            data.speedKmh.toString(),
            w * 0.27f,
            cy + r * 0.08f,
            p
        )

        c.drawText(
            String.format(
                Locale.US,
                "%.1f",
                data.rpm / 1000f
            ),
            w * 0.73f,
            cy + r * 0.08f,
            p
        )

        p.textSize = r * 0.12f
        p.color = Color.rgb(90, 200, 255)

        c.drawText(
            "km/h",
            w * 0.27f,
            cy + r * 0.24f,
            p
        )

        c.drawText(
            "x1000 RPM",
            w * 0.73f,
            cy + r * 0.24f,
            p
        )

        p.textSize = 26f
        p.color = Color.WHITE

        c.drawText(
            "PEUGEOT  •  VEHICLE OS",
            w * 0.5f,
            h * 0.12f,
            p
        )

        p.textSize = 14f
        p.typeface = Typeface.DEFAULT
        p.color = Color.rgb(100, 190, 235)

        c.drawText(
            "BMW INSPIRED DIGITAL COCKPIT",
            w * 0.5f,
            h * 0.12f + 24f,
            p
        )

        cards(c, h * 0.88f)
    }

    private fun gauge(
        c: Canvas,
        cx: Float,
        cy: Float,
        r: Float,
        value: Float,
        max: Float
    ) {

        p.style = Paint.Style.STROKE
        p.strokeWidth = r * 0.035f

        p.color = Color.rgb(25, 42, 56)

        c.drawArc(
            cx - r,
            cy - r,
            cx + r,
            cy + r,
            135f,
            270f,
            false,
            p
        )

        p.color = Color.rgb(70, 190, 255)

        c.drawArc(
            cx - r,
            cy - r,
            cx + r,
            cy + r,
            135f,
            270f * (value.coerceIn(0f, max) / max),
            false,
            p
        )

        for (i in 0..12) {

            val a = Math.toRadians(
                135.0 + i * 22.5
            )

            p.color = Color.rgb(130, 160, 175)

            p.strokeWidth =
                if (i % 3 == 0)
                    r * 0.022f
                else
                    r * 0.012f

            c.drawLine(
                cx + cos(a).toFloat() * r * 0.88f,
                cy + sin(a).toFloat() * r * 0.88f,
                cx + cos(a).toFloat() * r * 0.78f,
                cy + sin(a).toFloat() * r * 0.78f,
                p
            )
        }

        p.strokeWidth = r * 0.018f
        p.color = Color.WHITE

        val a = Math.toRadians(
            135.0 +
                270.0 *
                (value.coerceIn(0f, max) / max)
        )

        c.drawLine(
            cx,
            cy,
            cx + cos(a).toFloat() * r * 0.67f,
            cy + sin(a).toFloat() * r * 0.67f,
            p
        )
    }

    private fun cards(
        c: Canvas,
        y: Float
    ) {

        val labels = arrayOf(
            "ENGINE ${data.engineTempC}°C",
            "BATTERY ${
                String.format(
                    Locale.US,
                    "%.1fV",
                    data.batteryVoltage
                )
            }",
            "FUEL ${data.fuelPercent}%",
            "ECU ${data.ecuErrorCount} ERR",
            if (data.canConnected)
                "CAN ONLINE"
            else
                "CAN READY"
        )

        val gap = 12f
        val cw = (width - gap * 6f) / 5f

        labels.forEachIndexed { i, text ->

            val left =
                gap + i * (cw + gap)

            p.style = Paint.Style.FILL
            p.color = Color.rgb(10, 18, 27)

            c.drawRoundRect(
                left,
                y - 32,
                left + cw,
                y + 28,
                18f,
                18f,
                p
            )

            p.style = Paint.Style.STROKE
            p.strokeWidth = 1.5f
            p.color = Color.rgb(35, 70, 90)

            c.drawRoundRect(
                left,
                y - 32,
                left + cw,
                y + 28,
                18f,
                18f,
                p
            )

            p.style = Paint.Style.FILL
            p.color = Color.rgb(160, 185, 198)
            p.textSize = 12f

            c.drawText(
                text,
                left + cw / 2,
                y + 5,
                p
            )
        }
    }
}
