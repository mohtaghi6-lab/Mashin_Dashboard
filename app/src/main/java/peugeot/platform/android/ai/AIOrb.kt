val red = Color.red(color)
val green = Color.green(color)
val blue = Color.blue(color)

paint.shader = RadialGradient(
    cx,
    cy,
    radius,
    intArrayOf(
        Color.argb(230, red, green, blue),
        Color.argb(100, red, green, blue),
        Color.TRANSPARENT
    ),
    null,
    Shader.TileMode.CLAMP
)
