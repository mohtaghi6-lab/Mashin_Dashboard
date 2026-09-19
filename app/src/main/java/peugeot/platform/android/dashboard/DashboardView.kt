package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
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


class DashboardView(context: Context) : View(context) {


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = VehicleData.demo()

    private var pulse = 0f

    private var aiState = AIState.IDLE


    var onAIOrbClick: (() -> Unit)? = null


    private val blue = Color.rgb(70,190,255)
    private val lightBlue = Color.rgb(130,220,255)
    private val dark = Color.rgb(3,7,13)


    init {
        isFocusable = true
        postInvalidateOnAnimation()
    }


    fun setVehicleData(value: VehicleData) {

        data = value
        invalidate()

    }


    fun setAIState(value: AIState) {

        aiState = value
        invalidate()

    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)


        val w = width.toFloat()
        val h = height.toFloat()


        canvas.drawColor(dark)


        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = Color.WHITE
        paint.textSize = 28f


        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            w/2,
            50f,
            paint
        )


        drawAIOrb(
            canvas,
            w/2,
            h/2
        )


        paint.textSize = 55f
        paint.color = Color.WHITE


        canvas.drawText(
            "${data.speedKmh}",
            w/2,
            h*0.75f,
            paint
        )


        paint.textSize = 18f
        paint.color = lightBlue


        canvas.drawText(
            "km/h",
            w/2,
            h*0.75f + 35,
            paint
        )


        pulse += 0.05f

        postInvalidateOnAnimation()

    }
    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {

        val wave =
            ((sin(pulse.toDouble()) + 1) / 2).toFloat()


        paint.style = Paint.Style.STROKE

        paint.strokeWidth =
            3f + wave * 3f


        paint.color = Color.argb(
            100,
            70,
            190,
            255
        )


        canvas.drawCircle(
            cx,
            cy,
            70f + wave * 10f,
            paint
        )


        paint.strokeWidth = 4f
        paint.color = blue


        canvas.drawCircle(
            cx,
            cy,
            50f,
            paint
        )


        paint.style = Paint.Style.FILL


        paint.color =
            when(aiState){

                AIState.LISTENING ->
                    Color.rgb(0,220,255)

                AIState.THINKING ->
                    Color.rgb(255,180,50)

                AIState.SPEAKING ->
                    Color.rgb(80,255,150)

                AIState.ERROR ->
                    Color.RED

                else ->
                    blue
            }


        canvas.drawCircle(
            cx,
            cy,
            42f,
            paint
        )


        paint.color = Color.WHITE
        paint.textSize = 24f
        paint.textAlign = Paint.Align.CENTER


        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
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

            val dx =
                event.x - width/2f

            val dy =
                event.y - height/2f


            val distance =
                kotlin.math.sqrt(
                    dx*dx + dy*dy
                )


            if(distance < 100f){

                onAIOrbClick?.invoke()

                return true
            }
        }


        return true
    }

}
