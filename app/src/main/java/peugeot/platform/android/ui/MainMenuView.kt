package peugeot.platform.android

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

import peugeot.platform.android.ai.AIEngine
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.ai.SpeechManager
import peugeot.platform.android.ai.VoiceManager

import peugeot.platform.android.can.CANReceiver

import peugeot.platform.android.dashboard.BMWMenu
import peugeot.platform.android.dashboard.Dashboard3DView

import peugeot.platform.android.ui.CallPageView
import peugeot.platform.android.ui.CarPageView
import peugeot.platform.android.ui.ClockPageView
import peugeot.platform.android.ui.ErrorScannerPageView
import peugeot.platform.android.ui.HomePageView
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.ui.MusicPageView
import peugeot.platform.android.ui.NavigationPageView
import peugeot.platform.android.ui.StartupView
import peugeot.platform.android.ui.VehicleSettingsPageView

import peugeot.platform.android.vehicle.ErrorScannerEngine
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleDataController

import peugeot.platform.android.weather.WeatherManager

import peugeot.platform.android.steering.SteeringAction
import peugeot.platform.android.steering.SteeringWheelManager


class MainActivity : Activity() {


    private lateinit var root: FrameLayout

    private lateinit var startupView: StartupView

    // BMW MAIN
    private lateinit var dashboard: Dashboard3DView
    private lateinit var bmwMenu: BMWMenu
    private lateinit var homePageView: HomePageView
    private lateinit var clockPageView: ClockPageView


    // Pages
    private lateinit var carPageView: CarPageView
    private lateinit var musicPageView: MusicPageView
    private lateinit var navigationPageView: NavigationPageView
    private lateinit var callPageView: CallPageView
    private lateinit var errorScannerPageView: ErrorScannerPageView
    private lateinit var vehicleSettingsPageView: VehicleSettingsPageView


    // Vehicle
    private lateinit var vehicleDataController: VehicleDataController
    private lateinit var errorScannerEngine: ErrorScannerEngine
    private lateinit var weatherManager: WeatherManager


    // AI
    private lateinit var aiEngine: AIEngine
    private lateinit var voiceManager: VoiceManager
    private lateinit var speechManager: SpeechManager


    private lateinit var displayBootManager: DisplayBootManager


    private var pendingVoiceStart = false
    private var pendingVoiceCallName: String? = null



    companion object {

        private const val AUDIO_PERMISSION_REQUEST = 1001
        private const val CONTACTS_PERMISSION_REQUEST = 1002
        private const val PHONE_STATE_PERMISSION_REQUEST = 1003

    }



    private val phoneUpdateReceiver =
        object : BroadcastReceiver() {


            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {


                if (
                    intent?.action !=
                    "peugeot.platform.android.PHONE_STATE_UPDATE"
                ) return



                val state =
                    intent.getStringExtra("state")
                        ?: return


                val number =
                    intent.getStringExtra("number")
                        ?: ""



                when(state) {


                    TelephonyManager.EXTRA_STATE_RINGING -> {


                        val name =
                            findContactName(number)


                        if (::callPageView.isInitialized) {

                            callPageView
                                .showIncomingCall(
                                    name,
                                    number
                                )

                        }


                        showPage(
                            MainMenuPage.CALL
                        )

                    }



                    TelephonyManager.EXTRA_STATE_OFFHOOK -> {


                        if (::callPageView.isInitialized) {

                            callPageView
                                .showActiveCall(
                                    findContactName(number),
                                    number
                                )

                        }

                    }



                    TelephonyManager.EXTRA_STATE_IDLE -> {


                        if (::callPageView.isInitialized) {

                            callPageView
                                .clearIncomingCall()

                        }

                    }

                }

            }

        }



    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)



        requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY



        root =
            FrameLayout(this)

