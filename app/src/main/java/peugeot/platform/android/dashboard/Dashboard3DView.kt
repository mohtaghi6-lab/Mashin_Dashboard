package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class Dashboard3DView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var aiState =
        AIState.IDLE


    var onAIOrbClick:
            (() -> Unit)? = null



    fun setVehicleData(
        data: VehicleData
    ) {

        vehicleData =
            data

        invalidate()
    }



    fun setAIState(
        state: AIState
    ) {

        aiState =
            state

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



        drawBackground(
            canvas,
            w,
            h
        )



        drawGauge(
            canvas,
            w * 0.25f,
            h * 0.50f,
            min(w,h) * 0.20f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "SPEED"
        )



        drawGauge(
            canvas,
            w * 0.75f,
            h * 0.50f,
            min(w,h) * 0.20f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )



        drawCenterAI(
            canvas,
            w/2f,
            h*0.50f
        )



        postInvalidateDelayed(
            40L
        )

    }



    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.rgb(
                1,
                7,
                15
            )


        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )


        paint.color =
            Color.argb(
                45,
                0,
                150,
                255
            )


        canvas.drawCircle(
            w/2f,
            h/2f,
            min(w,h)*0.45f,
            paint
        )

    }
        private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maxValue: Float,
        title: String
    ) {


        val progress =
            (value / maxValue)
                .coerceIn(
                    0f,
                    1f
                )



        val rect =
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            22f


        paint.strokeCap =
            Paint.Cap.ROUND


        paint.color =
            Color.argb(
                50,
                100,
                200,
                255
            )


        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )



        paint.strokeWidth =
            8f


        paint.color =
            Color.CYAN



        canvas.drawArc(
            rect,
            135f,
            270f * progress,
            false,
            paint
        )



        // Tick marks

        paint.strokeWidth =
            2f


        paint.color =
            Color.argb(
                150,
                200,
                240,
                255
            )


        for(
            i in 0..30
        ){

            val angle =
                Math.toRadians(
                    135.0 +
                    i * 9.0
                )


            val r1 =
                radius - 15f


            val r2 =
                if(i % 5 == 0)
                    radius + 5f
                else
                    radius - 5f



            canvas.drawLine(

                cx +
                    cos(angle).toFloat()
                    * r1,

                cy +
                    sin(angle).toFloat()
                    * r1,


                cx +
                    cos(angle).toFloat()
                    * r2,

                cy +
                    sin(angle).toFloat()
                    * r2,


                paint
            )

        }



        // Needle

        val needle =
            Math.toRadians(
                135.0 +
                270.0 * progress
            )


        paint.strokeWidth =
            5f


        paint.color =
            Color.WHITE



        canvas.drawLine(

            cx,

            cy,


            cx +
                cos(needle).toFloat()
                *
                (radius - 30f),


            cy +
                sin(needle).toFloat()
                *
                (radius - 30f),


            paint

        )



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.CYAN


        canvas.drawCircle(
            cx,
            cy,
            10f,
            paint
        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        paint.textSize =
            radius * 0.25f



        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + 20f,
            paint
        )



        paint.textSize =
            18f


        paint.color =
            Color.CYAN



        canvas.drawText(
            title,
            cx,
            cy + radius * 0.45f,
            paint
        )

    }



    private fun drawCenterAI(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {


        val pulse =
    (System.currentTimeMillis() % 2000L)
        .toFloat() / 2000f



        val radius =
            65f +
            pulse * 10f



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                80,
                0,
                180,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            radius + 25f,
            paint
        )


        paint.color =
            Color.rgb(
                20,
                120,
                200
            )


        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            28f



        canvas.drawText(
            "MRT",
            cx,
            cy + 10f,
            paint
        )
                paint.textSize =
            12f


        paint.typeface =
            Typeface.DEFAULT


        paint.color =
            when(aiState) {

                AIState.LISTENING ->
                    Color.GREEN


                AIState.THINKING ->
                    Color.YELLOW


                AIState.SPEAKING ->
                    Color.CYAN


                AIState.ERROR ->
                    Color.RED


                else ->
                    Color.WHITE
            }



        val stateText =
            when(aiState) {

                AIState.IDLE ->
                    "READY"


                AIState.LISTENING ->
                    "LISTENING"


                AIState.THINKING ->
                    "THINKING"


                AIState.SPEAKING ->
                    "SPEAKING"


                AIState.ERROR ->
                    "ERROR"
            }



        canvas.drawText(
            stateText,
            cx,
            cy + 45f,
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

            onAIOrbClick?.invoke()

            performClick()

            return true

        }


        return true

    }




    override fun performClick():
            Boolean {

        super.performClick()

        return true

    }


}
