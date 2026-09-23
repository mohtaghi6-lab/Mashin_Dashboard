package peugeot.platform.android

/**
 * Shared swipe listener used by MainActivity's root touch handler.
 * MainActivity replaces the callbacks during initialization.
 */
val swipeController = SwipeController(
    onSwipeRight = {},
    onSwipeLeft = {}
)
