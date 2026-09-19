package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin


class HomePageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var pulse =
        0f


    private var speed =
        0


    private var battery =
        "12.6 V"


    private var canStatus =
        "CONNECTED"


    private var musicStatus =
        "Bluetooth"


    private val blue =
        Color.rgb(
            70,
            190,
            255
        )



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val w =
            width.toFloat()


        val h =
            height.toFloat()



        canvas.drawColor(
            Color.rgb(
                2,
                7,
                14
            )
        )


        pulse += 0.04f



        drawTopBar(
            canvas,
            w
        )


        drawAIOrb(
            canvas,
            w / 2,
            h / 2 - 70
        )


        drawVehicleInfo(
            canvas,
            w
        )


        drawCards(
            canvas,
            w,
            h
        )


        postInvalidateOnAnimation()

    }



    private fun drawTopBar(
        canvas: Canvas,
        width: Float
    ) {

        val time =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(
                Date()
            )


        val date =
            SimpleDateFormat(
                "yyyy/MM/dd",
                Locale.getDefault()
            ).format(
                Date()
            )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        paint.textSize =
            34f


        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            width / 2,
            60f,
            paint
        )


        paint.color =
            blue


        paint.textSize =
            22f


        canvas.drawText(
            time,
            width / 2,
            105f,
            paint
        )


        paint.color =
            Color.LTGRAY


        paint.textSize =
            14f


        canvas.drawText(
            date,
            width / 2,
            130f,
            paint
        )

    }
        private fun drawAIOrb(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {


        val glow =
            ((sin(pulse.toDouble()) + 1) / 2)
                .toFloat()



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            4f + glow * 5f


        paint.color =
            Color.argb(
                160,
                70,
                190,
                255
            )


        canvas.drawCircle(
            x,
            y,
            95f + glow * 15f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                80,
                70,
                190,
                255
            )


        canvas.drawCircle(
            x,
            y,
            70f,
            paint
        )



        paint.color =
            blue


        canvas.drawCircle(
            x,
            y,
            50f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            28f



        canvas.drawText(
            "AI",
            x,
            y + 10,
            paint
        )

    }




    private fun drawVehicleInfo(
    canvas: Canvas,
    width: Float
) {


    paint.textAlign =
        Paint.Align.CENTER


    paint.color =
        Color.WHITE


    paint.textSize =
        42f


    canvas.drawText(
        "$speed",
        width / 2,
        height.toFloat() / 2f + 70f,
        paint
    )


    paint.color =
        blue


    paint.textSize =
        18f


    canvas.drawText(
        "km/h",
        width / 2,
        height.toFloat() / 2f + 100f,
        paint
    )

}


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            42f


       canvas.drawText(
    "$speed",
    width / 2,
    height.toFloat() / 2f + 70f,
    paint
)


...


canvas.drawText(
    "km/h",
    width / 2,
    height.toFloat() / 2f + 100f,
    paint
)

    }




    private fun drawCards(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {


        drawGlassCard(
            canvas,
            40f,
            height - 220f,
            width / 2 - 20f,
            height - 80f,
            "BATTERY",
            battery
        )



        drawGlassCard(
            canvas,
            width / 2 + 20f,
            height - 220f,
            width - 40f,
            height - 80f,
            "CAN STATUS",
            canStatus
        )



        drawGlassCard(
            canvas,
            40f,
            height - 70f,
            width / 2 - 20f,
            height - 20f,
            "MUSIC",
            musicStatus
        )



        drawGlassCard(
            canvas,
            width / 2 + 20f,
            height - 70f,
            width - 40f,
            height - 20f,
            "NAVIGATION",
            "READY"
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


        val glow =
            ((sin(pulse.toDouble()) + 1) / 2)
                .toFloat()



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                130,
                20,
                35,
                55
            )


        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            25f,
            25f,
            paint
        )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            2f + glow


        paint.color =
            Color.argb(
                180,
                70,
                190,
                255
            )


        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            25f,
            25f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            16f


        canvas.drawText(
            title,
            (left + right) / 2,
            top + 28,
            paint
        )



        paint.color =
            blue


        paint.textSize =
            18f


        canvas.drawText(
            value,
            (left + right) / 2,
            top + 58,
            paint
        )

    }




    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ){

            val x =
                event.x


            val y =
                event.y



            if(
                x > width / 2 &&
                y > height - 220
            ){

                canStatus =
                    if(
                        canStatus ==
                        "CONNECTED"
                    )
                        "WAITING"
                    else
                        "CONNECTED"


                invalidate()

            }



            if(
                x < width / 2 &&
                y > height - 220
            ){

                musicStatus =
                    if(
                        musicStatus ==
                        "Bluetooth"
                    )
                        "PLAYING"
                    else
                        "Bluetooth"


                invalidate()

            }

        }


        return true

    }

}
