package peugeot.platform.android
import android.app.Activity
import android.os.Handler
import android.os.Looper
class DisplayBootManager(private val activity:Activity){
 private val handler=Handler(Looper.getMainLooper()); private var windowReady=false; private var restartStarted=false
 fun onWindowReady(){ if(windowReady)return; windowReady=true; handler.postDelayed({ if(!restartStarted){restartStarted=true; AndroidRebootController(activity).restartAppOnce()} },1500L) }
 fun destroy(){handler.removeCallbacksAndMessages(null)}
}
