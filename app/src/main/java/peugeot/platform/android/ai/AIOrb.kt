package peugeot.platform.android.ai

import android.content.Context
import android.graphics.*
import android.view.View
import kotlin.math.sin

class AIOrb(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var state: AIState = AIState.IDLE
        set(value) {
            field = value
            invalidate()
        }

    private var animation = 0f

    init {
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {

        val cx = width / 2f
        val cy = height / 2f

        val pulse =
            ((sin(animation.toDouble()) + 1.0) / 2.0).toFloat()

        val radius = 55f + pulse * 8f

        val color = when (state) {
            AIState.IDLE -> Color.rgb(60, 150, 220)
            AIState.LISTENING -> Color.rgb(70, 210, 255)
            AIState.THINKING -> Color.rgb(100, 170, 255)
            AIState.SPEAKING -> Color.rgb(80, 220, 190)
            AIState.ERROR -> Color.rgb(230, 80, 80)
        }

        paint.style = Paint.Style.FILL

        paint.shader = RadialGradient(
            cx,
            cy,
            radius,
            intArrayOf(
                Color.argb(230, color.red(), color.green(), color.blue()),
                Color.argb(100, color.red(), color.green(), color.blue()),
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.8f,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = color

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT_BOLD

        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            paint
        )

        paint.textSize = 11f
        paint.typeface = Typeface.DEFAULT

        canvas.drawText(
            when (state) {
                AIState.IDLE -> "آماده‌ام"
                AIState.LISTENING -> "گوش می‌کنم"
                AIState.THINKING -> "در حال پردازش"
                AIState.SPEAKING -> "در حال پاسخ"
                AIState.ERROR -> "خطا"
            },
            cx,
            cy + 30f,
            paint
        )

        animation += 0.04f
        postInvalidateOnAnimation()
    }
}
