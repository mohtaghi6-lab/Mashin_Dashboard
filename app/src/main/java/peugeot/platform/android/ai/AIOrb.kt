package peugeot.platform.android.ai

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin

class AIOrb @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var animation = 0f

    var state: AIState = AIState.IDLE
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f

        if (width <= 0 || height <= 0) {
            return
        }

        val baseRadius =
            minOf(width, height) / 2f * 0.55f

        val pulse =
            ((sin(animation.toDouble()) + 1.0) / 2.0).toFloat()

        val radius =
            baseRadius + pulse * 7f

        val orbColor = when (state) {
            AIState.IDLE ->
                Color.rgb(60, 150, 220)

            AIState.LISTENING ->
                Color.rgb(70, 210, 255)

            AIState.THINKING ->
                Color.rgb(100, 170, 255)

            AIState.SPEAKING ->
                Color.rgb(80, 220, 190)

            AIState.ERROR ->
                Color.rgb(230, 80, 80)
        }

        val red = Color.red(orbColor)
        val green = Color.green(orbColor)
        val blue = Color.blue(orbColor)

        // Outer glow
        paint.style = Paint.Style.FILL

        paint.shader = RadialGradient(
            cx,
            cy,
            radius * 1.8f,
            intArrayOf(
                Color.argb(220, red, green, blue),
                Color.argb(90, red, green, blue),
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

        // Main orb
        paint.shader = RadialGradient(
            cx - radius * 0.25f,
            cy - radius * 0.30f,
            radius * 1.25f,
            intArrayOf(
                Color.rgb(
                    minOf(255, red + 80),
                    minOf(255, green + 40),
                    255
                ),
                orbColor,
                Color.rgb(
                    red / 4,
                    green / 4,
                    blue / 4
                )
            ),
            floatArrayOf(
                0f,
                0.45f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.shader = null

        // Outer ring
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f + pulse * 2f
        paint.color = orbColor

        canvas.drawCircle(
            cx,
            cy,
            radius + 4f,
            paint
        )

        // AI text
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        paint.textSize = radius * 0.40f

        canvas.drawText(
            "AI",
            cx,
            cy + radius * 0.12f,
            paint
        )

        // Persian state
        paint.typeface = android.graphics.Typeface.DEFAULT
        paint.textSize = radius * 0.17f
        paint.color = Color.argb(
            230,
            255,
            255,
            255
        )

        val statusText = when (state) {
            AIState.IDLE -> "آماده‌ام"
            AIState.LISTENING -> "گوش می‌کنم"
            AIState.THINKING -> "در حال پردازش"
            AIState.SPEAKING -> "در حال پاسخ"
            AIState.ERROR -> "خطا"
        }

        canvas.drawText(
            statusText,
            cx,
            cy + radius * 0.52f,
            paint
        )

        animation += 0.045f

        if (animation > Math.PI * 2) {
            animation = 0f
        }

        postInvalidateOnAnimation()
    }
}
