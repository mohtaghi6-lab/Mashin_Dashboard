package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import peugeot.platform.android.navigation.NavigationDestination
import peugeot.platform.android.navigation.NavigationManager
import peugeot.platform.android.navigation.NavigationState
import kotlin.math.min

class NavigationPageView(
    context: Context
) : android.view.View(context) {

    var onBackClick: (() -> Unit)? = null

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var navigationState =
        NavigationManager.getState()

    private var destination =
        NavigationManager.getDestination()

    private var pressedButton =
        -1

    private val background =
        Color.rgb(3, 7, 13)

    private val panel =
        Color.rgb(8, 18, 28)

    private val border =
        Color.rgb(30, 70, 90)

    private val blue =
        Color.rgb(70, 190, 255)

    private val lightBlue =
        Color.rgb(135, 225, 255)

    private val green =
        Color.rgb(80, 220, 190)

    init {
        isFocusable = true
    }

    fun refresh() {

        navigationState =
            NavigationManager.getState()

        destination =
            NavigationManager.getDestination()

        invalidate()
    }

    fun setDestination(
        value: NavigationDestination
    ) {

        NavigationManager.setDestination(
            value
        )

        refresh()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        val w =
            width.toFloat()

        val h =
            height.toFloat()

        canvas.drawColor(
            background
        )

        drawBackground(
            canvas,
            w,
            h
        )

        drawHeader(
            canvas,
            w
        )

        drawMapArea(
            canvas,
            w,
            h
        )

        drawDestinationInfo(
            canvas,
            w,
            h
        )

        drawControls(
            canvas,
            w,
            h
        )

        drawFooter(
            canvas,
            w,
            h
        )
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val glowPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        glowPaint.shader =
            android.graphics.RadialGradient(
                w * 0.50f,
                h * 0.42f,
                w * 0.70f,
                intArrayOf(
                    Color.rgb(
                        7,
                        35,
                        52
                    ),
                    Color.rgb(
                        4,
                        16,
                        26
                    ),
                    background
                ),
                floatArrayOf(
                    0f,
                    0.52f,
                    1f
                ),
                android.graphics.Shader.TileMode.CLAMP
            )

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            glowPaint
        )
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {

        paint.style =
            Paint.Style.FILL

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textAlign =
            Paint.Align.LEFT

        paint.textSize =
            28f

        paint.color =
            Color.WHITE

        canvas.drawText(
            "NAVIGATION",
            38f,
            48f,
            paint
        )

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                100,
                175,
                210
            )

        canvas.drawText(
            "PEUGEOT INTELLIGENT NAV",
            40f,
            68f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.textSize =
            12f

        paint.color =
            when (
                navigationState
            ) {

                NavigationState.NAVIGATING ->
                    green

                NavigationState.READY ->
                    lightBlue

                NavigationState.ARRIVED ->
                    green

                NavigationState.IDLE ->
                    Color.rgb(
                        145,
                        160,
                        170
                    )
            }

        canvas.drawText(
            statusText(),
            w - 38f,
            50f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 26f
        paint.color = Color.WHITE

        canvas.drawText(
            "‹",
            12f,
            48f,
            paint
        )
    }

    private fun drawMapArea(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val left =
            35f

        val top =
            h * 0.16f

        val right =
            w - 35f

        val bottom =
            h * 0.64f

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.rgb(
                5,
                15,
                23
            )

        canvas.drawRoundRect(
            RectF(
                left,
                top,
                right,
                bottom
            ),
            26f,
            26f,
            paint
        )

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            1.5f

        paint.color =
            border

        canvas.drawRoundRect(
            RectF(
                left,
                top,
                right,
                bottom
            ),
            26f,
            26f,
            paint
        )

        /*
         * Map grid
         */

        paint.strokeWidth =
            1f

        paint.color =
            Color.rgb(
                15,
                40,
                53
            )

        var x =
            left + 35f

        while (x < right) {

            canvas.drawLine(
                x,
                top,
                x,
                bottom,
                paint
            )

            x += 55f
        }

        var y =
            top + 35f

        while (y < bottom) {

            canvas.drawLine(
                left,
                y,
                right,
                y,
                paint
            )

            y += 55f
        }

        /*
         * Main road
         */

        val road =
            Path()

        road.moveTo(
            left + 20f,
            bottom - 40f
        )

        road.cubicTo(
            w * 0.30f,
            h * 0.50f,
            w * 0.42f,
            h * 0.27f,
            w * 0.58f,
            h * 0.34f
        )

        road.cubicTo(
            w * 0.70f,
            h * 0.40f,
            w * 0.78f,
            h * 0.25f,
            right - 20f,
            top + 55f
        )

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            14f

        paint.color =
            Color.rgb(
                18,
                45,
                59
            )

        canvas.drawPath(
            road,
            paint
        )

        paint.strokeWidth =
            3f

        paint.color =
            blue

        canvas.drawPath(
            road,
            paint
        )

        /*
         * Current position
         */

        val currentX =
            w * 0.27f

        val currentY =
            h * 0.51f

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                70,
                70,
                190,
                255
            )

        canvas.drawCircle(
            currentX,
            currentY,
            28f,
            paint
        )

        paint.color =
            blue

        canvas.drawCircle(
            currentX,
            currentY,
            9f,
            paint
        )

        paint.color =
            Color.WHITE

        canvas.drawCircle(
            currentX,
            currentY,
            4f,
            paint
        )

        /*
         * Destination marker
         */

        val destinationX =
            w * 0.72f

        val destinationY =
            h * 0.29f

        paint.color =
            Color.argb(
                65,
                80,
                220,
                190
            )

        canvas.drawCircle(
            destinationX,
            destinationY,
            25f,
            paint
        )

        paint.color =
            green

        canvas.drawCircle(
            destinationX,
            destinationY,
            9f,
            paint
        )

        /*
         * Navigation arrow
         */

        val arrow =
            Path()

        arrow.moveTo(
            currentX,
            currentY - 18f
        )

        arrow.lineTo(
            currentX - 11f,
            currentY + 10f
        )

        arrow.lineTo(
            currentX,
            currentY + 5f
        )

        arrow.lineTo(
            currentX + 11f,
            currentY + 10f
        )

        arrow.close()

        paint.color =
            Color.WHITE

        canvas.drawPath(
            arrow,
            paint
        )
    }

    private fun drawDestinationInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val centerX =
            w * 0.50f

        val y =
            h * 0.69f

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            19f

        paint.color =
            Color.WHITE

        val destinationName =
            destination?.name
                ?: "No Destination"

        canvas.drawText(
            destinationName,
            centerX,
            y,
            paint
        )

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                100,
                155,
                180
            )

        val coordinates =
            destination?.let {
                String.format(
                    java.util.Locale.US,
                    "%.5f , %.5f",
                    it.latitude,
                    it.longitude
                )
            } ?: "Destination not selected"

        canvas.drawText(
            coordinates,
            centerX,
            y + 22f,
            paint
        )
    }

    private fun drawControls(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val centerX =
            w * 0.50f

        val y =
            h * 0.82f

        drawButton(
            canvas,
            centerX - 105f,
            y,
            0,
            "START"
        )

        drawButton(
            canvas,
            centerX,
            y,
            1,
            "STOP"
        )

        drawButton(
            canvas,
            centerX + 105f,
            y,
            2,
            "RESET"
        )
    }

    private fun drawButton(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        index: Int,
        label: String
    ) {

        val width =
            88f

        val height =
            48f

        val left =
            cx - width / 2f

        val top =
            cy - height / 2f

        val right =
            cx + width / 2f

        val bottom =
            cy + height / 2f

        paint.style =
            Paint.Style.FILL

        paint.color =
            if (
                pressedButton ==
                index
            )
                Color.rgb(
                    15,
                    55,
                    75
                )
            else
                panel

        canvas.drawRoundRect(
            RectF(
                left,
                top,
                right,
                bottom
            ),
            16f,
            16f,
            paint
        )

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            1.5f

        paint.color =
            when (index) {

                0 ->
                    green

                1 ->
                    Color.rgb(
                        230,
                        90,
                        90
                    )

                else ->
                    border
            }

        canvas.drawRoundRect(
            RectF(
                left,
                top,
                right,
                bottom
            ),
            16f,
            16f,
            paint
        )

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            11f

        paint.color =
            Color.WHITE

        canvas.drawText(
            label,
            cx,
            cy + 4f,
            paint
        )
    }

    private fun drawFooter(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.style =
            Paint.Style.FILL

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            10f

        paint.textAlign =
            Paint.Align.LEFT

        paint.color =
            Color.rgb(
                80,
                125,
                150
            )

        canvas.drawText(
            "MAP ENGINE READY",
            35f,
            h - 22f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.color =
            lightBlue

        canvas.drawText(
            "GPS • READY",
            w - 35f,
            h - 22f,
            paint
        )
    }

    private fun statusText():
        String {

        return when (
            navigationState
        ) {

            NavigationState.IDLE ->
                "● NAV STANDBY"

            NavigationState.READY ->
                "● ROUTE READY"

            NavigationState.NAVIGATING ->
                "● NAVIGATING"

            NavigationState.ARRIVED ->
                "● ARRIVED"
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            event.action ==
            MotionEvent.ACTION_DOWN
        ) {

            pressedButton =
                getButtonAt(
                    event.x,
                    event.y
                )

            invalidate()

            return true
        }

        if (
            event.action ==
            MotionEvent.ACTION_UP
        ) {

            if (
                event.x <= 70f &&
                event.y <= 80f
            ) {
                onBackClick?.invoke()
                pressedButton = -1
                invalidate()
                return true
            }

            val button =
                getButtonAt(
                    event.x,
                    event.y
                )

            if (
                button ==
                pressedButton
            ) {

                performAction(
                    button
                )
            }

            pressedButton =
                -1

            invalidate()

            return true
        }

        return true
    }

    private fun getButtonAt(
        x: Float,
        y: Float
    ): Int {

        val w =
            width.toFloat()

        val h =
            height.toFloat()

        val centerX =
            w * 0.50f

        val centerY =
            h * 0.82f

        val positions =
            floatArrayOf(
                centerX - 105f,
                centerX,
                centerX + 105f
            )

        for (
            i in positions.indices
        ) {

            if (
                x >=
                positions[i] - 50f &&
                x <=
                positions[i] + 50f &&
                y >=
                centerY - 30f &&
                y <=
                centerY + 30f
            ) {

                return i
            }
        }

        return -1
    }

    private fun performAction(
        button: Int
    ) {

        when (button) {

            0 -> {

                if (
                    destination == null
                ) {

                    setDestination(
                        NavigationDestination(
                            name =
                                "Demo Destination",
                            latitude =
                                35.6892,
                            longitude =
                                51.3890
                        )
                    )
                }

                NavigationManager.startNavigation()
            }

            1 -> {

                NavigationManager.stopNavigation()
            }

            2 -> {

                NavigationManager.reset()
            }
        }

        refresh()
    }
}
