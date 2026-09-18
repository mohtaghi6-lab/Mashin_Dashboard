package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.bluetooth.BluetoothManager
import peugeot.platform.android.bluetooth.MusicInfo
import peugeot.platform.android.bluetooth.MusicManager
import peugeot.platform.android.bluetooth.PlaybackState

class MusicPageView(
    context: Context
) : View(context) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var music =
        MusicManager.getCurrentMusic()

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

    private var pressedButton =
        -1

    init {
        isFocusable = true
    }

    fun setMusic(
        value: MusicInfo
    ) {
        music = value
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

        canvas.drawColor(
            background
        )

        drawBackgroundGlow(
            canvas,
            w,
            h
        )

        drawHeader(
            canvas,
            w
        )

        drawAlbumPanel(
            canvas,
            w,
            h
        )

        drawTrackInfo(
            canvas,
            w,
            h
        )

        drawControls(
            canvas,
            w,
            h
        )

        drawBluetoothStatus(
            canvas,
            w,
            h
        )
    }

    private fun drawBackgroundGlow(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val glow =
            Paint(Paint.ANTI_ALIAS_FLAG)

        glow.shader =
            android.graphics.RadialGradient(
                w * 0.5f,
                h * 0.40f,
                w * 0.65f,
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
                    0.5f,
                    1f
                ),
                android.graphics.Shader.TileMode.CLAMP
            )

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            glow
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
            "MUSIC",
            38f,
            48f,
            paint
        )

        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                100,
                175,
                210
            )

        canvas.drawText(
            "MEDIA CENTER",
            40f,
            68f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.textSize =
            12f

        val connected =
            BluetoothManager.isConnected()

        paint.color =
            if (connected)
                green
            else
                Color.rgb(
                    145,
                    160,
                    170
                )

        canvas.drawText(
            if (connected)
                "● BLUETOOTH CONNECTED"
            else
                "● BLUETOOTH STANDBY",
            w - 38f,
            50f,
            paint
        )
    }

    private fun drawAlbumPanel(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val size =
            minOf(
                w * 0.30f,
                h * 0.43f
            )

        val left =
            w * 0.12f

        val top =
            h * 0.22f

        val right =
            left + size

        val bottom =
            top + size

        paint.style =
            Paint.Style.FILL

        paint.color =
            panel

        canvas.drawRoundRect(
            RectF(
                left,
                top,
                right,
                bottom
            ),
            28f,
            28f,
            paint
        )

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            2f

        paint.color =
            border

        canvas.drawRoundRect(
            RectF(
                left,
                top,
                right,
                bottom
            ),
            28f,
            28f,
            paint
        )

        /*
         * Album visualization
         */

        val cx =
            (left + right) / 2f

        val cy =
            (top + bottom) / 2f

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            3f

        paint.color =
            blue

        canvas.drawCircle(
            cx,
            cy,
            size * 0.28f,
            paint
        )

        paint.strokeWidth =
            1.5f

        paint.color =
            Color.rgb(
                40,
                100,
                130
            )

        canvas.drawCircle(
            cx,
            cy,
            size * 0.20f,
            paint
        )

        paint.style =
            Paint.Style.FILL

        paint.color =
            blue

        canvas.drawCircle(
            cx,
            cy,
            size * 0.06f,
            paint
        )

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            18f

        paint.color =
            Color.WHITE

        canvas.drawText(
            "♫",
            cx,
            cy + size * 0.47f,
            paint
        )
    }

    private fun drawTrackInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val centerX =
            w * 0.67f

        val centerY =
            h * 0.38f

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            28f

        paint.color =
            Color.WHITE

        val title =
            if (music.title.isBlank())
                "No Music"
            else
                music.title

        canvas.drawText(
            title,
            centerX,
            centerY,
            paint
        )

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            15f

        paint.color =
            lightBlue

        val artist =
            if (music.artist.isBlank())
                "Unknown Artist"
            else
                music.artist

        canvas.drawText(
            artist,
            centerX,
            centerY + 32f,
            paint
        )

        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                90,
                135,
                155
            )

        canvas.drawText(
            playbackText(),
            centerX,
            centerY + 58f,
            paint
        )
    }

    private fun playbackText():
        String {

        return when (
            music.state
        ) {

            PlaybackState.PLAYING ->
                "NOW PLAYING"

            PlaybackState.PAUSED ->
                "PAUSED"

            PlaybackState.STOPPED ->
                "STOPPED"
        }
    }

    private fun drawControls(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val centerX =
            w * 0.67f

        val y =
            h * 0.68f

        val spacing =
            105f

        drawControlButton(
            canvas,
            centerX - spacing,
            y,
            0,
            "◀"
        )

        drawControlButton(
            canvas,
            centerX - spacing / 3f,
            y,
            1,
            if (
                music.state ==
                PlaybackState.PLAYING
            )
                "Ⅱ"
            else
                "▶"
        )

        drawControlButton(
            canvas,
            centerX + spacing / 3f,
            y,
            2,
            "■"
        )

        drawControlButton(
            canvas,
            centerX + spacing,
            y,
            3,
            "▶"
        )
    }

    private fun drawControlButton(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        index: Int,
        icon: String
    ) {

        val radius =
            if (index == 1)
                34f
            else
                27f

        paint.style =
            Paint.Style.FILL

        paint.color =
            if (pressedButton == index)
                Color.rgb(
                    15,
                    55,
                    75
                )
            else
                panel

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            if (index == 1)
                2.5f
            else
                1.5f

        paint.color =
            if (pressedButton == index)
                blue
            else
                border

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            if (index == 1)
                20f
            else
                16f

        paint.color =
            Color.WHITE

        canvas.drawText(
            icon,
            cx,
            cy + 6f,
            paint
        )
    }

    private fun drawBluetoothStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.LEFT

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                80,
                125,
                150
            )

        canvas.drawText(
            if (
                BluetoothManager.isConnected()
            )
                "AUDIO LINK ACTIVE"
            else
                "CONNECT PHONE FOR MUSIC",
            35f,
            h - 24f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.color =
            lightBlue

        canvas.drawText(
            "MEDIA SESSION READY",
            w - 35f,
            h - 24f,
            paint
        )
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
            w * 0.67f

        val centerY =
            h * 0.68f

        val spacing =
            105f

        val positions =
            arrayOf(
                centerX - spacing,
                centerX - spacing / 3f,
                centerX + spacing / 3f,
                centerX + spacing
            )

        for (
            i in positions.indices
        ) {

            val dx =
                x - positions[i]

            val dy =
                y - centerY

            val distance =
                dx * dx + dy * dy

            if (
                distance <=
                45f * 45f
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
                MusicManager.previous()
            }

            1 -> {

                if (
                    music.state ==
                    PlaybackState.PLAYING
                ) {

                    MusicManager.pause()

                } else {

                    MusicManager.resume()
                }
            }

            2 -> {
                MusicManager.stop()
            }

            3 -> {
                MusicManager.next()
            }
        }

        music =
            MusicManager.getCurrentMusic()

        invalidate()
    }
}
