package peugeot.platform.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: ""

        val prefs = context.getSharedPreferences("phone_state", Context.MODE_PRIVATE)

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                prefs.edit()
                    .putBoolean("incoming", true)
                    .putString("number", number)
                    .apply()
            }

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                prefs.edit()
                    .putBoolean("incoming", false)
                    .apply()
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                prefs.edit()
                    .putBoolean("incoming", false)
                    .putString("number", "")
                    .apply()
            }
        }

        val updateIntent = Intent("peugeot.platform.android.PHONE_STATE_UPDATE")
            .setPackage(context.packageName)
            .putExtra("state", state)
            .putExtra("number", number)

        context.sendBroadcast(updateIntent)
    }
}
