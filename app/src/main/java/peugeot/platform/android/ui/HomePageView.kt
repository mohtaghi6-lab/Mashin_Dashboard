package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.sin


class HomePageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var pulse =
        0f


    private val blue =
        Color.rgb(
            70,
            190,
            255
        )



    fun setVehicleData(
        data: VehicleData
    ) {

        vehicleData =
            data

        invalidate()

    }



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val width =
            this.width.toFloat()


        val height =
            this.height.toFloat()



        canvas.drawColor(
            Color.rgb(
                3,
                7,
                13
            )
        )



        drawHeader(
            canvas,
            width
        )


        drawAIOrb(
            canvas,
            width / 2f,
            height / 2f - 80f
        )
drawSpeedGauge(
    canvas,
    width / 2f,
    height / 2f + 20f
)
drawRpmGauge(
    canvas,
    width / 2f,
    height / 2f + 260f
)

        drawVehicleInfo(
            canvas,
            width,
            height
        )


        drawCards(
            canvas,
            width,
            height
        )


        pulse += 0.04f


        postInvalidateOnAnimation()

    }
        private fun drawHeader(
        canvas: Canvas,
        width: Float
    ) {

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.color =
            Color.WHITE

        paint.textSize =
            30f


        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            width / 2f,
            60f,
            paint
        )
    }




    private fun drawAIOrb(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {


        val wave =
            ((sin(pulse.toDouble()) + 1) / 2)
                .toFloat()



        // BMW outer glow

        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            4f + wave * 6f


        paint.color =
            Color.argb(
                120,
                0,
                170,
                255
            )


        canvas.drawCircle(
            x,
            y,
            95f + wave * 18f,
            paint
        )



        // Glass ring

        paint.strokeWidth =
            2f


        paint.color =
            Color.argb(
                180,
                70,
                220,
                255
            )


        canvas.drawCircle(
            x,
            y,
            75f,
            paint
        )



        // Orb body

        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                130,
                0,
                120,
                220
            )


        canvas.drawCircle(
            x,
            y,
            60f,
            paint
        )



        // Inner animated light

        paint.color =
            when {

                pulse % 6 < 2 ->
                    Color.rgb(
                        0,
                        220,
                        255
                    )


                pulse % 6 < 4 ->
                    Color.rgb(
                        80,
                        180,
                        255
                    )


                else ->
                    Color.rgb(
                        0,
                        150,
                        255
                    )
            }



        canvas.drawCircle(
            x,
            y,
            45f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            26f


        paint.typeface =
            Typeface.DEFAULT_BOLD



        canvas.drawText(
            "AI",
            x,
            y + 9f,
            paint
        )

    }




    private fun drawVehicleInfo(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            55f



        canvas.drawText(
            "${vehicleData.speedKmh}",
            width / 2f,
            height / 2f + 80f,
            paint
        )



        paint.color =
            blue


        paint.textSize =
            18f



        canvas.drawText(
            "km/h",
            width / 2f,
            height / 2f + 115f,
            paint
        )



        paint.color =
            Color.LTGRAY


        paint.textSize =
            16f



        canvas.drawText(
            "RPM : ${vehicleData.rpm}",
            width / 2f,
            height / 2f + 150f,
            paint
        )


        canvas.drawText(
            "TEMP : ${vehicleData.engineTempC} °C",
            width / 2f,
            height / 2f + 180f,
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
            height - 180f,
            width / 2f - 20f,
            height - 60f,
            "BATTERY",
            "${vehicleData.batteryVoltage} V"
        )



        drawGlassCard(
            canvas,
            width / 2f + 20f,
            height - 180f,
            width - 40f,
            height - 60f,
            "CAN",
            if(vehicleData.canConnected)
                "CONNECTED"
            else
                "WAITING"
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


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                120,
                25,
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
            2f


        paint.color =
            blue


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
            (left + right) / 2f,
            top + 35f,
            paint
        )



        paint.color =
            blue


        paint.textSize =
            18f


        canvas.drawText(
            value,
            (left + right) / 2f,
            top + 75f,
            paint
        )

    }




    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ) {

            return true

        }


        return true

    }
private fun drawSpeedGauge(
    canvas: Canvas,
    x: Float,
    y: Float
) {


    val speed =
        vehicleData.speedKmh.coerceIn(0, 260)


    val angle =
        -220f + (speed / 260f) * 260f



    // Outer BMW ring

    paint.style =
        Paint.Style.STROKE


    paint.strokeWidth =
        8f


    paint.color =
        Color.argb(
            160,
            0,
            170,
            255
        )


    canvas.drawCircle(
        x,
        y,
        130f,
        paint
    )



    // Inner ring

    paint.strokeWidth =
        3f


    paint.color =
        Color.WHITE


    canvas.drawCircle(
        x,
        y,
        110f,
        paint
    )



    // Needle

    val rad =
        Math.toRadians(angle.toDouble())


    val needleX =
        x + kotlin.math.cos(rad).toFloat() * 95f


    val needleY =
        y + kotlin.math.sin(rad).toFloat() * 95f



    paint.strokeWidth =
        5f


    paint.color =
        Color.RED



    canvas.drawLine(
        x,
        y,
        needleX,
        needleY,
        paint
    )



    // Speed text

    paint.style =
        Paint.Style.FILL


    paint.textAlign =
        Paint.Align.CENTER


    paint.color =
        Color.WHITE


    paint.textSize =
        45f



    canvas.drawText(
        "$speed",
        x,
        y + 15f,
        paint
    )



    paint.textSize =
        16f


    paint.color =
        blue



    canvas.drawText(
        "km/h",
        x,
        y + 45f,
        paint
    )

}
private fun drawRpmGauge(
    canvas: Canvas,
    x: Float,
    y: Float
) {


    val rpm =
        vehicleData.rpm.coerceIn(
            0,
            8000
        )


    val angle =
        -220f + (rpm / 8000f) * 260f



    // Outer ring

    paint.style =
        Paint.Style.STROKE


    paint.strokeWidth =
        7f


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
        90f,
        paint
    )



    // Inner ring

    paint.strokeWidth =
        3f


    paint.color =
        Color.WHITE


    canvas.drawCircle(
        x,
        y,
        75f,
        paint
    )



    // Needle

    val rad =
        Math.toRadians(
            angle.toDouble()
        )


    val needleX =
        x +
        kotlin.math.cos(rad).toFloat()
        * 65f


    val needleY =
        y +
        kotlin.math.sin(rad).toFloat()
        * 65f



    paint.strokeWidth =
        4f


    paint.color =
        Color.RED



    canvas.drawLine(
        x,
        y,
        needleX,
        needleY,
        paint
    )



    // RPM text

    paint.style =
        Paint.Style.FILL


    paint.textAlign =
        Paint.Align.CENTER


    paint.color =
        Color.WHITE


    paint.textSize =
        28f



    canvas.drawText(
        "${rpm}",
        x,
        y + 10f,
        paint
    )



    paint.textSize =
        13f


    paint.color =
        blue



    canvas.drawText(
        "RPM",
        x,
        y + 35f,
        paint
    )

}
}
