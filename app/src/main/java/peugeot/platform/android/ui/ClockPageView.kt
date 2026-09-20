package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


class ClockPageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private val blue =
        Color.rgb(70, 190, 255)


    private val cyan =
        Color.rgb(120, 230, 255)


    private val dark =
        Color.rgb(2, 6, 12)



    private var running = true



    init {
        startAnimation()
    }



    private fun startAnimation() {

        postDelayed(
            object : Runnable {

                override fun run() {

                    invalidate()

                    if (running) {
                        postDelayed(
                            this,
                            1000
                        )
                    }
                }

            },
            1000
        )

    }





    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val w =
            width.toFloat()

        val h =
            height.toFloat()


        val cx =
            w / 2f


        val cy =
            h / 2f



        drawBackground(
            canvas,
            w,
            h
        )


        drawLuxuryClock(
            canvas,
            cx,
            cy
        )


        drawInformation(
            canvas,
            w,
            h
        )

    }





    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        canvas.drawColor(
            dark
        )


        val glow =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )


        glow.shader =
            RadialGradient(
                w / 2f,
                h / 2f,
                w * 0.55f,
                Color.rgb(
                    10,
                    55,
                    85
                ),
                dark,
                Shader.TileMode.CLAMP
            )


        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            glow
        )

    }





    private fun drawLuxuryClock(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {


        val radius =
            width.coerceAtMost(height)
                .toFloat() * 0.28f



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            8f


        paint.color =
            Color.argb(
                120,
                70,
                190,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            radius + 35f,
            paint
        )



        paint.strokeWidth =
            5f

        paint.color =
            Color.WHITE


        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        paint.strokeWidth =
            2f


        paint.color =
            cyan


        canvas.drawCircle(
            cx,
            cy,
            radius - 25f,
            paint
        )



        drawNumbers(
            canvas,
            cx,
            cy,
            radius
        )



        val calendar =
            Calendar.getInstance()



        val hour =
            calendar.get(Calendar.HOUR)


        val minute =
            calendar.get(Calendar.MINUTE)


        val second =
            calendar.get(Calendar.SECOND)



        drawHand(
            canvas,
            cx,
            cy,
            radius * 0.5f,
            (hour + minute / 60f) * 30f,
            10f
        )


        drawHand(
            canvas,
            cx,
            cy,
            radius * 0.7f,
            minute * 6f,
            6f
        )


        drawHand(
            canvas,
            cx,
            cy,
            radius * 0.82f,
            second * 6f,
            2f
        )



        paint.style =
            Paint.Style.FILL


        paint.color =
            blue


        canvas.drawCircle(
            cx,
            cy,
            12f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            22f


        paint.typeface =
            Typeface.DEFAULT_BOLD


        canvas.drawText(
            "MRT",
            cx,
            cy + 90f,
            paint
        )

    }





   private fun drawNumbers(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    radius: Float
) {

    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.CENTER
    paint.textSize = 28f
    paint.color = Color.WHITE

    for (i in 1..12) {

        val angle = Math.toRadians(
            (i * 30 - 90).toDouble()
        )

        val x = cx + cos(angle).toFloat() * (radius - 55f)

        val y = cy + sin(angle).toFloat() * (radius - 55f) + 10f

        canvas.drawText(
            i.toString(),
            x,
            y,
            paint
        )
    }
}





    private fun drawHand(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        length: Float,
        angle: Float,
        width: Float
    ) {


        val rad =
            Math.toRadians(
                angle.toDouble() - 90
            )


        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            width


        paint.strokeCap =
            Paint.Cap.ROUND


        paint.color =
            Color.WHITE



        canvas.drawLine(
            cx,
            cy,
            cx + cos(rad).toFloat() * length,
            cy + sin(rad).toFloat() * length,
            paint
        )

    }





    private fun drawInformation(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {


        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            blue


        paint.textSize =
            16f



        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            w / 2f,
            h - 90f,
            paint
        )



        paint.color =
            Color.LTGRAY


        paint.textSize =
            13f



        canvas.drawText(
            SimpleDateFormat(
                "EEEE dd MMMM",
                Locale.ENGLISH
            ).format(Date()),
            w / 2f,
            h - 65f,
            paint
        )

    }





    override fun onDetachedFromWindow() {

        running = false

        super.onDetachedFromWindow()

    }

}
