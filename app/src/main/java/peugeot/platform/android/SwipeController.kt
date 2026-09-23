package peugeot.platform.android

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

/** Handles horizontal swipe gestures on the activity root. */
class SwipeController(
    var onSwipeRight: () -> Unit,
    var onSwipeLeft: () -> Unit
) : View.OnTouchListener {

    private var downX = 0f
    private var downY = 0f
    private val threshold = 120f

    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }

            MotionEvent.ACTION_UP -> {
                val diffX = event.x - downX
                val diffY = event.y - downY

                if (abs(diffX) > abs(diffY)) {
                    when {
                        diffX > threshold -> onSwipeRight()
                        diffX < -threshold -> onSwipeLeft()
                    }
                }
                return true
            }

            MotionEvent.ACTION_CANCEL -> return true
        }

        return true
    }
}
