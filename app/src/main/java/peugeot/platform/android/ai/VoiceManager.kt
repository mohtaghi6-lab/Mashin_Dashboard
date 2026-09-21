package peugeot.platform.android.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer


class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onStateChanged: (AIState) -> Unit
) {


    private var speechRecognizer: SpeechRecognizer? = null

    private var listening = false



    private val listener =
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



            override fun onRmsChanged(
                rmsdB: Float
            ) {

            }



            override fun onBufferReceived(
                buffer: ByteArray?
            ) {

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


                if(listening){

                    restartListening()

                }

            }



            override fun onResults(
                results: Bundle?
            ) {


                val text =
                    results
                        ?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )
                        ?.firstOrNull()
                        ?: ""



                if(
                    text.isNotBlank()
                ){

                    onResult(
                        text
                    )

                }



                if(listening){

                    restartListening()

                }


            }



            override fun onPartialResults(
                partialResults: Bundle?
            ) {

            }



            override fun onEvent(
                eventType: Int,
                params: Bundle?
            ) {

            }


        }





    init {


        if(
            SpeechRecognizer.isRecognitionAvailable(
                context
            )
        ){

            speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(
                    context
                )


            speechRecognizer?.setRecognitionListener(
                listener
            )


        }


    }





    fun startContinuousListening(){


        listening = true


        startListening()


    }





    private fun startListening(){


        if(
            !listening
        ){

            return

        }



        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            )



        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )



        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "fa-IR"
        )



        intent.putExtra(
            RecognizerIntent.EXTRA_PARTIAL_RESULTS,
            true
        )



        intent.putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            1
        )



        speechRecognizer?.startListening(
            intent
        )


    }





    private fun restartListening(){


        if(
            !listening
        ){

            return

        }



        speechRecognizer?.cancel()



        Handler(
            Looper.getMainLooper()
        )
            .postDelayed({

                startListening()

            },700)



    }





    fun resumeContinuousListening(){


        if(
            listening
        ){

            startListening()

        }


    }





    fun stopListening(){


        listening = false


        speechRecognizer?.stopListening()



        onStateChanged(
            AIState.IDLE
        )


    }





    fun destroy(){


        listening = false


        speechRecognizer?.destroy()


        speechRecognizer = null


    }


}
