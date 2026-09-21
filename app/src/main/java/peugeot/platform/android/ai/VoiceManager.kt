package peugeot.platform.android.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale


class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onStateChanged: (AIState) -> Unit
) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val handler =
        Handler(Looper.getMainLooper())

    private var continuousMode = false


    fun startContinuousListening() {

        continuousMode = true

        createRecognizer()

        startListening()

    }


    private fun createRecognizer() {

        speechRecognizer?.destroy()

        speechRecognizer =
            SpeechRecognizer.createSpeechRecognizer(
                context
            )


        speechRecognizer?.setRecognitionListener(
            object : RecognitionListener {


                override fun onReadyForSpeech(
                    params: Bundle?
                ) {

                    onStateChanged(
                        AIState.LISTENING
                    )
                }


                override fun onBeginningOfSpeech() {

                    onStateChanged(
                        AIState.LISTENING
                    )
                }


                override fun onEndOfSpeech() {

                    onStateChanged(
                        AIState.THINKING
                    )
                }


                override fun onError(
                    error: Int
                ) {

                    onStateChanged(
                        AIState.IDLE
                    )


                    if (continuousMode) {

                        handler.postDelayed({

                            startListening()

                        },1000)

                    }

                }


                override fun onResults(
                    results: Bundle?
                ) {

                    val list =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )


                    val text =
                        list?.firstOrNull()
                            ?: ""


                    if(text.isNotBlank()) {

                        onResult(text)

                    }


                }


                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}


                override fun onRmsChanged(
                    rmsdB: Float
                ) {}


                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {}


                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}

            }
        )

    }



    private fun startListening() {

        if(!continuousMode)
            return


        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            )


        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "fa-IR"
        )


        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )


        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true
        )


        speechRecognizer?.startListening(
            intent
        )

    }



    fun resumeContinuousListening() {

        if(continuousMode) {

            handler.postDelayed({

                startListening()

            },800)

        }

    }



    fun stopRecognizerOnly(){

        speechRecognizer?.stopListening()

    }



    fun destroy(){

        continuousMode=false

        handler.removeCallbacksAndMessages(
            null
        )

        speechRecognizer?.destroy()

        speechRecognizer=null

    }

}
