package peugeot.platform.android.ui

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.content.ClipData
import android.content.ClipboardManager
import android.widget.EditText
import android.view.MotionEvent
import android.view.View

import peugeot.platform.android.vehicle.VehicleData


class CallPageView(
    context: Context
) : View(context) {


    var onBackClick: (() -> Unit)? = null
    var onCallClick: (() -> Unit)? = null
    var onDialerClick: (() -> Unit)? = null
    var onContactNumberReady: ((String) -> Unit)? = null
    var onEndClick: (() -> Unit)? = null
    var onContactsClick: (() -> Unit)? = null


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private val blue =
        Color.rgb(70,190,255)

    private val green =
        Color.rgb(80,220,170)

    private val red =
        Color.rgb(235,90,95)


    private var connected =
        false

    private var inCall =
        false

    private var incomingCall =
        false

    private var incomingNumber =
        ""


    private var contactName =
        "No Device"

    private var bluetoothReady =
        false



    private val backRect =
        RectF()

    private val numberRect =
        RectF()

    private val callRect =
        RectF()

    private val endRect =
        RectF()



    init {

        isClickable = true
        isFocusable = true

    }



    fun setVehicleData(
        data: VehicleData
    ){

        vehicleData = data

        invalidate()
    }




    override fun onDraw(
        canvas: Canvas
    ){

        super.onDraw(canvas)


        val w =
            width.toFloat()

        val h =
            height.toFloat()



        drawBackground(
            canvas,
            w,
            h
        )


        drawHeader(
            canvas,
            w
        )


        drawPhoneOrb(
            canvas,
            w,
            h
        )


        updateBluetoothStatus()

        drawStatus(
            canvas,
            w,
            h
        )


        drawVehicleInfo(
            canvas,
            w,
            h
        )


        drawControls(
            canvas,
            w,
            h
        )

        drawNumberHint(
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
    ){

        paint.shader =
            LinearGradient(
                0f,
                0f,
                0f,
                h,
                Color.rgb(2,5,12),
                Color.rgb(8,25,42),
                Shader.TileMode.CLAMP
            )


        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )


        paint.shader=null


    }




    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ){

        drawGlassPanel(
            canvas,
            28f,
            25f,
            w-28f,
            105f,
            30f
        )


        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            26f


        paint.color =
            Color.WHITE


        canvas.drawText(
            "PHONE",
            w/2f,
            65f,
            paint
        )



        paint.textSize =
            12f


        paint.color =
            blue


        canvas.drawText(
            "BMW LUXURY CALL SYSTEM",
            w/2f,
            88f,
            paint
        )


    }





    private fun drawPhoneOrb(
        canvas: Canvas,
        w: Float,
        h: Float
    ){

        val cx =
            w/2f

        val cy =
            h*0.35f


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                70,
                0,
                170,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            130f,
            paint
        )



        paint.color =
            Color.rgb(
                0,
                120,
                240
            )


        canvas.drawCircle(
            cx,
            cy,
            70f,
            paint
        )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            4f


        paint.color =
            if (incomingCall)
                blue
            else if(inCall)
                green
            else
                blue


        canvas.drawCircle(
            cx,
            cy,
            70f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            55f


        paint.color =
            Color.WHITE


        canvas.drawText(
            "☎",
            cx,
            cy+18f,
            paint
        )


    }





    private fun drawStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ){

        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            24f


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        canvas.drawText(
            contactName,
            w/2f,
            h*0.58f,
            paint
        )



        paint.textSize =
            13f


        paint.color =
            if(inCall)
                green
            else
                blue



        canvas.drawText(
            if (incomingCall)
                "INCOMING CALL"
            else if(inCall)
                "CALL IN PROGRESS"
            else
                "READY",
            w/2f,
            h*0.63f,
            paint
        )

        paint.textSize = 11f
        paint.color = Color.LTGRAY
        canvas.drawText(
            if (bluetoothReady)
                "BLUETOOTH • READY"
            else
                "BLUETOOTH • CHECK SYSTEM",
            w / 2f,
            h * 0.67f,
            paint
        )

    }




    fun showIncomingCall(name: String, number: String) {
        incomingCall = true
        inCall = false
        connected = true
        contactName = if (name.isBlank()) "تماس‌گیرنده" else name
        incomingNumber = number
        invalidate()
    }

    fun clearIncomingCall() {
        incomingCall = false
        incomingNumber = ""
        connected = false
        inCall = false
        contactName = "No Device"
        invalidate()
    }

    fun showContacts() {
        if (
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
            context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val names = ArrayList<String>()
        val numbers = ArrayList<String>()

        val cursor = context.contentResolver.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(
                android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val numberIndex = it.getColumnIndex(
                android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (it.moveToNext()) {
                val name = if (nameIndex >= 0) it.getString(nameIndex) else "بدون نام"
                val number = if (numberIndex >= 0) it.getString(numberIndex) else ""
                if (number.isNotBlank()) {
                    names.add(name ?: "بدون نام")
                    numbers.add(number)
                }
            }
        }

        if (names.isEmpty()) {
            android.app.AlertDialog.Builder(context)
                .setTitle("مخاطبین")
                .setMessage("مخاطبی برای نمایش پیدا نشد.")
                .setPositiveButton("باشه", null)
                .show()
            return
        }

        android.app.AlertDialog.Builder(context)
            .setTitle("مخاطبین")
            .setItems(names.toTypedArray()) { _, which ->
                val number = numbers[which]
                contactName = names[which]
                connected = true
                inCall = true
                onContactNumberReady?.invoke(number)
                onDialerClick?.invoke()
                invalidate()
            }
            .setNegativeButton("انصراف", null)
            .show()
    }

    private fun showNumberInput() {
        val input = EditText(context).apply {
            hint = "شماره تلفن"
            inputType = InputType.TYPE_CLASS_PHONE
            setSingleLine(true)
        }

        val dialog = android.app.AlertDialog.Builder(context)
            .setTitle("شماره‌گیری")
            .setView(input)
            .setNegativeButton("انصراف", null)
            .setPositiveButton("تماس") { _, _ ->
                val number = input.text.toString().trim()
                if (number.isNotEmpty()) {
                    contactName = number
                    connected = true
                    inCall = true
                    onContactNumberReady?.invoke(number)
                    onDialerClick?.invoke()
                    invalidate()
                }
            }
            .create()

        dialog.setOnShowListener {
            input.requestFocus()
            dialog.window?.setSoftInputMode(
                android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            )
        }

        dialog.show()
    }

    private fun updateBluetoothStatus() {
        bluetoothReady = try {
            BluetoothAdapter.getDefaultAdapter()?.isEnabled == true
        } catch (_: Exception) {
            false
        }
    }

    private fun drawVehicleInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ){

        paint.textAlign =
            Paint.Align.CENTER

        paint.textSize =
            14f

        paint.color =
            Color.LTGRAY


        canvas.drawText(
            "SPEED ${vehicleData.speedKmh} km/h   RPM ${vehicleData.rpm}",
            w/2f,
            h*0.70f,
            paint
        )

    }




    private fun drawNumberHint(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 11f
        paint.color = Color.LTGRAY
        canvas.drawText(
            "برای شماره‌گیری، دکمه CALL را بزن",
            w / 2f,
            h * 0.90f,
            paint
        )
    }

    private fun drawControls(
        canvas: Canvas,
        w: Float,
        h: Float
    ){

        val y = h * 0.78f

        if (incomingCall) {
            callRect.set(w * 0.10f, y - 35f, w * 0.43f, y + 35f)
            endRect.set(w * 0.57f, y - 35f, w * 0.90f, y + 35f)

            drawButton(canvas, callRect, "ANSWER", green)
            drawButton(canvas, endRect, "DECLINE", red)
        } else {
            callRect.set(w * 0.08f, y - 35f, w * 0.33f, y + 35f)
            numberRect.set(w * 0.38f, y - 35f, w * 0.62f, y + 35f)
            endRect.set(w * 0.67f, y - 35f, w * 0.92f, y + 35f)

            drawButton(canvas, callRect, "DIAL", green)
            drawButton(canvas, numberRect, "CONTACTS", blue)
            drawButton(canvas, endRect, "END", red)
        }
    }

    private fun drawButton(
        canvas: Canvas,
        rect: RectF,
        text:String,
        color:Int
    ){

        paint.color =
            Color.argb(
                140,
                15,
                35,
                55
            )


        canvas.drawRoundRect(
            rect,
            25f,
            25f,
            paint
        )


        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            2f


        paint.color =
            color


        canvas.drawRoundRect(
            rect,
            25f,
            25f,
            paint
        )


        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            15f


        paint.color =
            Color.WHITE


        canvas.drawText(
            text,
            rect.centerX(),
            rect.centerY()+5f,
            paint
        )

    }




    private fun drawFooter(
        canvas:Canvas,
        w:Float,
        h:Float
    ){

        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            11f


        paint.color =
            blue


        canvas.drawText(
            "PEUGEOT VEHICLE OS • BMW MODE",
            w/2f,
            h-30f,
            paint
        )

    }




    private fun drawGlassPanel(
        canvas:Canvas,
        l:Float,
        t:Float,
        r:Float,
        b:Float,
        radius:Float
    ){

        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                100,
                20,
                40,
                60
            )


        canvas.drawRoundRect(
            l,
            t,
            r,
            b,
            radius,
            radius,
            paint
        )

    }




    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (event.action == MotionEvent.ACTION_UP) {

            if (backRect.contains(event.x, event.y)) {
                onBackClick?.invoke()
                invalidate()
                performClick()
                return true
            }

            if (incomingCall) {
                if (callRect.contains(event.x, event.y)) {
                    incomingCall = false
                    inCall = true
                    onCallClick?.invoke()
                    invalidate()
                    return true
                }

                if (endRect.contains(event.x, event.y)) {
                    incomingCall = false
                    inCall = false
                    onEndClick?.invoke()
                    invalidate()
                    return true
                }
            } else {
                if (callRect.contains(event.x, event.y)) {
                    showNumberInput()
                    return true
                }

                if (numberRect.contains(event.x, event.y)) {
                    onContactsClick?.invoke()
                    return true
                }

                if (endRect.contains(event.x, event.y)) {
                    inCall = false
                    onEndClick?.invoke()
                    invalidate()
                    return true
                }
            }
        }

        performClick()
        return true
    }

    override fun performClick():
            Boolean {

        super.performClick()

        return true
    }



}
