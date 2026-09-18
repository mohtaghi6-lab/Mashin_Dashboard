package peugeot.platform.android.ai

import android.content.Context

class AIEngine(
    private val context: Context
) {

    fun process(
        text: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        if (text.isBlank()) {
            onError("متنی دریافت نشد")
            return
        }

        /*
         * موتور AI
         *
         * فعلاً این بخش آماده اتصال به API است.
         * در مرحله بعد OpenAI/Gemini را به این قسمت متصل می‌کنیم.
         */

        val response = when {
            text.contains("سلام") ->
                "سلام، خوشحالم که صدای من را صدا زدی."

            text.contains("خوبی") ->
                "ممنون، من آماده‌ام بهت کمک کنم."

            text.contains("اسم") ->
                "من دستیار هوشمند پژو پارس هستم."

            else ->
                "صدات رو شنیدم. آماده‌ام درخواستت رو انجام بدم."
        }

        onResponse(response)
    }
}
