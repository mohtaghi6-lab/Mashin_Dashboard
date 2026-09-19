package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import android.view.View

class MainMenuView(
    context: Context
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var currentPage = MainMenuPage.HOME

    var onPageSelected:
        ((MainMenuPage) -> Unit)? = null

    private val menuItems = arrayOf(
        MainMenuPage.HOME,
        MainMenuPage.CAR,
        MainMenuPage.MUSIC,
        MainMenuPage.NAVIGATION,
        MainMenuPage.CALL,
        MainMenuPage.SCAN,
        MainMenuPage.SETTINGS
    )

    private val labels = arrayOf(
        "HOME",
        "CAR",
        "MUSIC",
        "NAV",
        "CALL",
        "SCAN",
        "SET"
    )

    private val icons = arrayOf(
        "⌂",
        "◉",
        "♫",
        "⌖",
        "☎",
        "⌁",
        "⚙"
    )

    init {
        background = ColorDrawable(Color.TRANSPARENT)
        isClickable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        val barHeight = 78f
        val barLeft = 24f
        val barRight = w - 24f
        val barTop = h - barHeight - 18f
        val barBottom = h - 18f
        val radius = 26f

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(198, 8, 18, 31)

        canvas.drawRoundRect(
            barLeft,
            barTop,
            barRight,
            barBottom,
            radius,
            radius,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.2f
        paint.color = Color.argb(
            100,
            95,
            200,
            255
        )

        canvas.drawRoundRect(
            barLeft,
            barTop,
            barRight,
            barBottom,
            radius,
            radius,
            paint
        )

        val itemWidth =
            (barRight - barLeft) / menuItems.size

        for (i in menuItems.indices) {
            val cx =
                barLeft + itemWidth * i + itemWidth / 2f

            val selected =
                menuItems[i] == currentPage

            paint.style = Paint.Style.FILL

            if (selected) {
                paint.color = Color.argb(
                    90,
                    35,
                    175,
                    245
                )

                canvas.drawRoundRect(
                    cx - itemWidth * 0.39f,
                    barTop + 8f,
                    cx + itemWidth * 0.39f,
                    barBottom - 8f,
                    18f,
                    18f,
                    paint
                )
            }

            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 20f
            paint.color =
                if (selected) Color.rgb(145, 235, 255)
                else Color.rgb(160, 180, 195)

            canvas.drawText(
                icons[i],
                cx,
                barTop + 31f,
                paint
            )

            paint.typeface = Typeface.DEFAULT
            paint.textSize = 8.5f
            paint.color =
                if (selected) Color.WHITE
                else Color.rgb(120, 145, 160)

            canvas.drawText(
                labels[i],
                cx,
                barTop + 52f,
                paint
            )
        }
    }

    fun setPage(page: MainMenuPage) {
        currentPage = page
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val barTop = height.toFloat() - 96f

        if (event.y < barTop) {
            return false
        }

        if (event.action == MotionEvent.ACTION_UP) {
            val itemWidth =
                width.toFloat() / menuItems.size

            val index =
                (event.x / itemWidth).toInt()
                    .coerceIn(0, menuItems.lastIndex)

            currentPage = menuItems[index]
            onPageSelected?.invoke(currentPage)
            invalidate()
        }

        return true
    }
}
