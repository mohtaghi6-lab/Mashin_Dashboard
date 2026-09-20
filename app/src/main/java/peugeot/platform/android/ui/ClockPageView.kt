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
        Color.rgb(70,190,255)


    private var timer =
        Timer()


    init {

        timer.scheduleAtFixedRate(
            object : TimerTask() {

                override fun run() {
                    postInvalidate()
                }

            },
            0,
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


        canvas.drawColor(
            Color.rgb(
                2,
                6,
                12
            )
        )


        // Glow اطراف ساعت

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
            220f,
            paint
        )


        // بدنه ساعت

        paint.strokeWidth =
            4f

        paint.color =
            Color.WHITE


        canvas.drawCircle(
            cx,
            cy,
            190f,
            paint
        )


        val now =
            Calendar.getInstance()


        val hour =
            now.get(
                Calendar.HOUR
            )

        val minute =
            now.get(
                Calendar.MINUTE
            )

        val second =
            now.get(
                Calendar.SECOND
            )


        drawHand(
            canvas,
            cx,
            cy,
            110f,
            (hour + minute / 60f) * 30f,
            8f
        )


        drawHand(
            canvas,
            cx,
            cy,
            145f,
            minute * 6f,
            5f
        )


        drawHand(
            canvas,
            cx,
            cy,
            160f,
            second * 6f,
            2f
        )


        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.textSize =
            32f

        paint.color =
            Color.WHITE


        canvas.drawText(
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(
                Date()
            ),
            cx,
            cy + 270f,
            paint
        )


        paint.textSize =
            14f

        paint.color =
            blue


        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            cx,
            cy + 300f,
            paint
        )

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
            cx +
                    cos(rad).toFloat()
                    *
                    length,

            cy +
                    sin(rad).toFloat()
                    *
                    length,

            paint
        )

    }

}
