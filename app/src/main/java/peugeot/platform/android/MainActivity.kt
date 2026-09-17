package peugeot.platform.android
import android.app.Activity
import android.os.Bundle
class MainActivity : Activity() {
 private lateinit var displayBootManager: DisplayBootManager
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.activity_main); displayBootManager=DisplayBootManager(this) }
 override fun onWindowFocusChanged(hasFocus:Boolean){ super.onWindowFocusChanged(hasFocus); if(hasFocus) displayBootManager.onWindowReady() }
 override fun onDestroy(){ if(::displayBootManager.isInitialized) displayBootManager.destroy(); super.onDestroy() }
}
