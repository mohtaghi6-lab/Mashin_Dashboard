```kotlin
package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CallPageView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val blue = Color.rgb(70, 190, 255)
    private val dark = Color.rgb(3, 7, 13)
    private val panel = Color.rgb(9, 17, 27)

    private var connected = false
    private var contactName = "No Device"

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()


        // Background

        canvas.drawColor(dark)


        // Top title

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = Color.WHITE
        paint.textSize = 28f

        canvas.drawText(
            "PHONE",
            w / 2,
            55f,
            paint
        )


        paint.textSize = 13f
        paint.color = Color.rgb(120,185,215)

        canvas.drawText(
            "BLUETOOTH CALL SYSTEM",
            w / 2,
            78f,
            paint
        )


        // Phone circle

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(8,35,55)

        canvas.drawCircle(
            w / 2,
            h * 0.38f,
            90f,
            paint
        )


        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.color = blue

        canvas.drawCircle(
            w / 2,
            h * 0.38f,
            90f,
            paint
        )


        // Phone icon

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 55f

        canvas.drawText(
            "☎",
            w / 2,
            h * 0.38f + 20f,
            paint
        )


        // Status

        paint.textSize = 22f
        paint.color = blue

        canvas.drawText(
            if (connected)
                contactName
            else
                "Disconnected",
            w / 2,
            h * 0.55f,
            paint
        )


        // Buttons

        drawButton(
            canvas,
            w * 0.35f,
            h * 0.72f,
            "CALL"
        )

        drawButton(
            canvas,
            w * 0.65f,
            h * 0.72f,
            "END"
        )


        // Bottom

        paint.textSize = 12f
        paint.color = Color.rgb(100,150,175)

        canvas.drawText(
            "PEUGEOT VEHICLE OS • PHONE",
            w / 2,
            h - 30f,
            paint
        )
    }


    private fun drawButton(
        canvas: Canvas,
        x: Float,
        y: Float,
        text: String
    ) {

        paint.style = Paint.Style.FILL
        paint.color = panel

        canvas.drawRoundRect(
            x - 70f,
            y - 35f,
            x + 70f,
            y + 35f,
            20f,
            20f,
            paint
        )


        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = blue

        canvas.drawRoundRect(
            x - 70f,
            y - 35f,
            x + 70f,
            y + 35f,
            20f,
            20f,
            paint
        )


        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 16f
        paint.color = Color.WHITE

        canvas.drawText(
            text,
            x,
            y + 6f,
            paint
        )
    }


    fun setBluetoothState(
        state: Boolean,
        name: String
    ) {

        connected = state
        contactName = name

        invalidate()
    }
}
```
