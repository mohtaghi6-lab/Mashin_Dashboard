package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

class MainMenuView(
    context: Context
) : View(context) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var currentPage =
        MainMenuPage.HOME

    var onPageSelected:
        ((MainMenuPage) -> Unit)? = null

    private val blue =
        Color.rgb(70, 190, 255)

    private val background =
        Color.rgb(3, 7, 13)

    private val panel =
        Color.rgb(9, 17, 27)

    fun setPage(
        page: MainMenuPage
    ) {
        currentPage = page
        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)

        canvas.drawColor(background)

        val w = width.toFloat()
        val h = height.toFloat()

        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.typeface =
            Typeface.DEFAULT_BOLD
        paint.textAlign =
            Paint.Align.CENTER
        paint.textSize = 28f

        canvas.drawText(
            pageTitle(),
            w / 2f,
            h * 0.20f,
            paint
        )

        paint.textSize = 12f
        paint.color =
            Color.rgb(110, 165, 195)

        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            w / 2f,
            h * 0.20f + 25f,
            paint
        )

        drawMenu(
            canvas,
            w,
            h
        )
    }

    private fun pageTitle(): String {

        return when (currentPage) {

            MainMenuPage.HOME ->
                "HOME"

            MainMenuPage.CAR ->
                "CAR"

            MainMenuPage.MUSIC ->
                "MUSIC"

            MainMenuPage.NAVIGATION ->
                "NAVIGATION"

            MainMenuPage.CALL ->
                "CALL"

            MainMenuPage.SCAN ->
                "DIAGNOSTICS"
        }
    }

    private fun drawMenu(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val labels = arrayOf(
            "HOME",
            "CAR",
            "MUSIC",
            "NAV",
            "CALL",
            "SCAN"
        )

        val pages = arrayOf(
            MainMenuPage.HOME,
            MainMenuPage.CAR,
            MainMenuPage.MUSIC,
            MainMenuPage.NAVIGATION,
            MainMenuPage.CALL,
            MainMenuPage.SCAN
        )

        val gap = 12f
        val margin = 30f
        val cardWidth =
            (w - margin * 2f - gap * 5f) / 6f

        val top = h * 0.72f
        val bottom = top + 70f

        for (i in labels.indices) {

            val left =
                margin +
                i * (cardWidth + gap)

            val right =
                left + cardWidth

            paint.style =
                Paint.Style.FILL

            paint.color =
                if (currentPage == pages[i])
                    Color.rgb(13, 48, 67)
                else
                    panel

            canvas.drawRoundRect(
                left,
                top,
                right,
                bottom,
                16f,
                16f,
                paint
            )

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 1.5f

            paint.color =
                if (currentPage == pages[i])
                    blue
                else
                    Color.rgb(30, 65, 82)

            canvas.drawRoundRect(
                left,
                top,
                right,
                bottom,
                16f,
                16f,
                paint
            )

            paint.style =
                Paint.Style.FILL

            paint.textSize = 11f

            paint.color =
                if (currentPage == pages[i])
                    Color.WHITE
                else
                    Color.rgb(150, 175, 190)

            canvas.drawText(
                labels[i],
                (left + right) / 2f,
                top + 42f,
                paint
            )
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            event.action !=
            MotionEvent.ACTION_UP
        ) {
            return true
        }

        val w = width.toFloat()
        val h = height.toFloat()

        val gap = 12f
        val margin = 30f

        val cardWidth =
            (w - margin * 2f - gap * 5f) / 6f

        val top = h * 0.72f
        val bottom = top + 70f

        val pages = arrayOf(
            MainMenuPage.HOME,
            MainMenuPage.CAR,
            MainMenuPage.MUSIC,
            MainMenuPage.NAVIGATION,
            MainMenuPage.CALL,
            MainMenuPage.SCAN
        )

        for (i in pages.indices) {

            val left =
                margin +
                i * (cardWidth + gap)

            val right =
                left + cardWidth

            if (
                event.x >= left &&
                event.x <= right &&
                event.y >= top &&
                event.y <= bottom
            ) {

                setPage(pages[i])

                onPageSelected?.invoke(
                    pages[i]
                )

                return true
            }
        }

        return true
    }
}
