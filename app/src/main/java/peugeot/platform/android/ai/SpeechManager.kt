package peugeot.platform.android.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechManager(
    context: Context,
    private val onStateChanged: (AIState) -> Unit
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var ready = false

    init {
        textToSpeech = TextToSpeech(
            context,
            this
        )
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val result = textToSpeech?.setLanguage(
                Locale("fa", "IR")
            )

            ready =
                result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED

        } else {

            ready = false
            onStateChanged(AIState.ERROR)
        }
    }

    fun speak(text: String) {

        if (!ready || text.isBlank()) {
            onStateChanged(AIState.ERROR)
            return
        }

        onStateChanged(AIState.SPEAKING)

        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "peugeot_ai_response"
        )
    }

    fun stop() {

        textToSpeech?.stop()

        onStateChanged(
            AIState.IDLE
        )
    }

    fun destroy() {

        textToSpeech?.stop()
        textToSpeech?.shutdown()

        textToSpeech = null
        ready = false
    }
}
