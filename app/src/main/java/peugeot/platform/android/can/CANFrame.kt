package peugeot.platform.android.can


data class CANFrame(

    val id: Int,

    val data: ByteArray,

    val timestamp: Long = System.currentTimeMillis()

)
