package com.example.ecotracker.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

object TranslatorHelper {

    fun translateToPt(
        text: String,
        onSuccess: (String) -> Unit,
        onError: () -> Unit
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.PORTUGUESE)
            .build()

        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translated ->
                        onSuccess(translated)
                    }
                    .addOnFailureListener {
                        onError()
                    }
            }
            .addOnFailureListener {
                onError()
            }
    }
}
