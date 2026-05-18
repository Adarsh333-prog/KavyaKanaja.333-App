package com.app.kavyakanaja.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.app.kavyakanaja.BuildConfig

object GeminiHelper {

    private val model = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun explainPoem(
        kannadaText: String,
        englishMeaning: String,
        bhavartha: String
    ): String {
        val prompt = """
            You are a Kannada literature expert. Explain this Kannada poem in simple English.
            
            Poem in Kannada:
            $kannadaText
            
            English Meaning:
            $englishMeaning
            
            Bhavartha:
            $bhavartha
            
            Please provide:
            1. Simple explanation of the poem's theme
            2. Key literary elements used
            3. Why this poem is significant
            4. Life lesson from this poem
            
            Keep it simple and easy to understand.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text ?: "Could not generate explanation"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun getWordMeaning(
        word: String,
        poemContext: String
    ): String {
        return try {
            val prompt = "What does the Kannada word \"$word\" mean in English? Give only the meaning in 1 line."
            val response = model.generateContent(prompt)
            response.text?.trim() ?: "Meaning not found"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}