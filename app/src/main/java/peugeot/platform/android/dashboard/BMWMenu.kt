package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class BMWMenu(
    context: Context
) : View(context) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val items =
        arrayOf(
            "CAR",
            "MUSIC",
            "NAVI",
            "PHONE",
            "SCAN",
            "AI"
        )

    private var selected =
        0

    var onMenuClick:
            ((String) -> Unit)? = null


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

        val radius =
            minOf(w, h) * 0.30f


        // --------------------------------------------------
        // DARK GLASS BACKGROUND
        // --------------------------------------------------

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                45,
                0,
                25,
                45
            )

        canvas.drawCircle(
            cx,
            cy,
            radius + 35f,
            paint
        )


        // --------------------------------------------------
        // OUTER BLUE GLOW
        // --------------------------------------------------

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            10f

        paint.color =
            Color.argb(
                35,
                0,
                170,
                255
            )

        canvas.drawCircle(
            cx,
            cy,
            radius + 18f,
            paint
        )


        // --------------------------------------------------
        // MAIN GLASS CIRCLE
        // --------------------------------------------------

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                90,
                255,
                255,
                255
            )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )


        // --------------------------------------------------
        // GLASS BORDER
        // --------------------------------------------------

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            2.5f

        paint.color =
            Color.argb(
                180,
                0,
                190,
                255
            )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )


        // --------------------------------------------------
        // INNER RING
        // --------------------------------------------------

        paint.strokeWidth =
            1.5f

        paint.color =
            Color.argb(
                100,
                150,
                220,
                255
            )

        canvas.drawCircle(
            cx,
            cy,
            radius - 18f,
            paint
        )


        // --------------------------------------------------
        // MENU ITEMS
        // --------------------------------------------------

        val itemRadius =
            radius - 55f

        for (
            i in items.indices
        ) {

            val angle =
                Math.toRadians(
                    (-90 + i * 60).toDouble()
                )

            val x =
                cx +
                        cos(angle).toFloat() *
                        itemRadius

            val y =
                cy +
                        sin(angle).toFloat() *
                        itemRadius


            val isSelected =
                i == selected


            // Selection glow

            if (isSelected) {

                paint.style =
                    Paint.Style.FILL

                paint.color =
                    Color.argb(
                        70,
                        0,
                        190,
                        255
                    )

                canvas.drawCircle(
                    x,
                    y - 5f,
                    38f,
                    paint
                )


                paint.style =
                    Paint.Style.STROKE

                paint.strokeWidth =
                    3f

                paint.color =
                    Color.CYAN

                canvas.drawCircle(
                    x,
                    y - 5f,
                    34f,
                    paint
                )
            }


            // Item text

            paint.style =
                Paint.Style.FILL

            paint.textAlign =
                Paint.Align.CENTER

            paint.typeface =
                Typeface.DEFAULT_BOLD

            paint.textSize =
                if (isSelected) {
                    19f
                } else {
                    16f
                }

            paint.color =
                if (isSelected) {
                    Color.CYAN
                } else {
                    Color.WHITE
                }


            canvas.drawText(
                items[i],
                x,
                y + 5f,
                paint
            )
        }


        // --------------------------------------------------
        // CENTER AI GLOW
        // --------------------------------------------------

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                55,
                0,
                170,
                255
            )

        canvas.drawCircle(
            cx,
            cy,
            72f,
            paint
        )


        // --------------------------------------------------
        // CENTER AI CIRCLE
        // --------------------------------------------------

        paint.color =
            Color.rgb(
                0,
                125,
                235
            )

        canvas.drawCircle(
            cx,
            cy,
            55f,
            paint
        )


        // --------------------------------------------------
        // CENTER AI BORDER
        // --------------------------------------------------

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            3f

        paint.color =
            Color.CYAN

        canvas.drawCircle(
            cx,
            cy,
            55f,
            paint
        )


        // --------------------------------------------------
        // AI TEXT
        // --------------------------------------------------

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            25f

        paint.color =
            Color.WHITE

        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            paint
        )


        // --------------------------------------------------
        // SELECTED LABEL
        // --------------------------------------------------

        paint.textSize =
            11f

        paint.color =
            Color.argb(
                190,
                180,
                230,
                255
            )

        canvas.drawText(
            items[selected],
            cx,
            cy + 88f,
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

            val cx =
                width / 2f

            val cy =
                height / 2f


            val dx =
                event.x - cx

            val dy =
                event.y - cy


            val distance =
                kotlin.math.sqrt(
                    dx * dx +
                            dy * dy
                )


            // Center AI button

            if (
                distance < 65f
            ) {

                selected =
                    5

                invalidate()

                onMenuClick?.invoke(
                    "AI"
                )

                performClick()

                return true
            }


            // Outside menu

            if (
                distance < 70f ||
                distance > minOf(
                    width,
                    height
                ) * 0.45f
            ) {

                performClick()

                return true
            }


            val angle =
                Math.toDegrees(
                    atan2(
                        dy,
                        dx
                    ).toDouble()
                )


            var normalized =
                angle + 90.0

            if (
                normalized < 0.0
            ) {
                normalized += 360.0
            }


            val index =
                (
                    (normalized + 30.0) / 60.0
                ).toInt() % items.size


            selected =
                index


            invalidate()


            onMenuClick?.invoke(
                items[selected]
            )


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
