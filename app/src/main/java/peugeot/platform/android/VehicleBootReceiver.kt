package peugeot.platform.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VehicleBootReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val launchIntent =
            context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?: return

        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP
        )

        context.startActivity(launchIntent)
    }
}
