package peugeot.platform.android
import android.app.Activity
import android.content.Intent
class AndroidRebootController(private val activity:Activity){
 companion object { private const val PREFS_NAME="peugeot_vehicle_os_boot"; private const val KEY_RESTART_DONE="startup_restart_done" }
 fun restartAppOnce(){ val prefs=activity.getSharedPreferences(PREFS_NAME,Activity.MODE_PRIVATE); if(prefs.getBoolean(KEY_RESTART_DONE,false))return; prefs.edit().putBoolean(KEY_RESTART_DONE,true).apply(); val launchIntent=activity.packageManager.getLaunchIntentForPackage(activity.packageName)?:return; launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK); activity.startActivity(launchIntent); activity.finish() }
 fun resetStartupRestartFlag(){activity.getSharedPreferences(PREFS_NAME,Activity.MODE_PRIVATE).edit().remove(KEY_RESTART_DONE).apply()}
}
