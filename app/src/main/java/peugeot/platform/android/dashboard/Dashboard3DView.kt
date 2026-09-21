package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.min


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



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.rgb(
                2,
                8,
                16
            )


        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )



        drawGauge(
            canvas,
            w * 0.25f,
            h / 2f,
            min(w,h) * 0.18f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "KM/H"
        )


        drawGauge(
            canvas,
            w * 0.75f,
            h / 2f,
            min(w,h) * 0.18f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )



        paint.textAlign =
            Paint.Align.CENTER

        paint.textSize =
            40f

        paint.color =
            Color.CYAN


        canvas.drawText(
            "MRT",
            w/2f,
            h/2f,
            paint
        )


        postInvalidateDelayed(
            40L
        )

    }



    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        max: Float,
        text: String
    ) {


        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            18f


        paint.color =
            Color.argb(
                70,
                100,
                200,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )


        val progress =
            (value / max)
                .coerceIn(
                    0f,
                    1f
                )


        paint.color =
            Color.CYAN


        canvas.drawArc(
            RectF(
                cx-radius,
                cy-radius,
                cx+radius,
                cy+radius
            ),
            -90f,
            360f * progress,
            false,
            paint
        )


        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            radius * 0.35f


        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy,
            paint
        )


        paint.textSize =
            18f


        paint.color =
            Color.CYAN


        canvas.drawText(
            text,
            cx,
            cy + 35f,
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


            onAIOrbClick?.invoke()

            performClick()

            return true

        }


        return true

    }



    override fun performClick(): Boolean {

        super.performClick()

        return true

    }

}
