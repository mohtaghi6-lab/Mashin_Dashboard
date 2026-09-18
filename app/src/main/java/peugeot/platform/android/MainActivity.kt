package peugeot.platform.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.ai.VoiceManager
import peugeot.platform.android.dashboard.DashboardView
import peugeot.platform.android.vehicle.VehicleData

class MainActivity : Activity() {

    private lateinit var displayBootManager: DisplayBootManager

    private lateinit var voiceManager: VoiceManager

    private lateinit var dashboard: DashboardView

    companion object {
        private const val AUDIO_PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        dashboard = DashboardView(this)

        dashboard.setVehicleData(
            VehicleData.demo()
        )

        setContentView(dashboard)
        
dashboard.setOnTouchListener { _, event ->

    if (event.action == android.view.MotionEvent.ACTION_UP) {

        if (
            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            voiceManager.startListening()
        } else {
            requestMicrophonePermission()
        }

        true
    } else {
        true
    }
}
        displayBootManager = DisplayBootManager()
        displayBootManager.onWindowReady()

        voiceManager = VoiceManager(
            context = this,

            onResult = { text ->
                runOnUiThread {
                    // متن تشخیص داده‌شده فارسی
                    // در مرحله بعد به موتور AI ارسال می‌شود.
                }
            },

            onStateChanged = { state ->
                runOnUiThread {
                    updateAIState(state)
                }
            }
        )

        requestMicrophonePermission()
    }

    private fun requestMicrophonePermission() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.M
        ) {

            if (
                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO
                    ),
                    AUDIO_PERMISSION_REQUEST
                )
            }
        }
    }

    private fun updateAIState(state: AIState) {
        // اتصال AI Orb در مرحله بعد
        when (state) {

            AIState.IDLE -> {
                // آماده
            }

            AIState.LISTENING -> {
                // در حال گوش دادن
            }

            AIState.THINKING -> {
                // پردازش
            }

            AIState.SPEAKING -> {
                // پاسخ صوتی
            }

            AIState.ERROR -> {
                // خطای صوتی
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == AUDIO_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // میکروفون آماده است.
        }
    }

    override fun onDestroy() {

        if (::voiceManager.isInitialized) {
            voiceManager.destroy()
        }

        if (::displayBootManager.isInitialized) {
            displayBootManager.destroy()
        }

        super.onDestroy()
    }
}
