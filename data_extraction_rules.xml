package com.app.kavyakanaja.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object WikipediaImageFetcher {

    private const val TAG = "WikipediaImageFetcher"

    private val poetWikipediaNames = mapOf(
        "kuvempu" to listOf(
            "Kuvempu",
            "K._V._Puttappa",
            "Kuppali_Venkatappa_Puttappa"
        ),
        "da. ra. bendre" to listOf(
            "D._R._Bendre",
            "Dattatreya_Ramachandra_Bendre",
            "D._R._Bendre"
        ),
        "v. k. gokak" to listOf(
            "V._K._Gokak",
            "Vinayaka_Krishna_Gokak"
        ),
        "u. r. ananthamurthy" to listOf(
            "U._R._Ananthamurthy",
            "Udupi_Rajagopalacharya_Ananthamurthy"
        ),
        "girish karnad" to listOf(
            "Girish_Karnad",
            "Girish_Raghunath_Karnad"
        ),
        "chandrashekhara kambara" to listOf(
            "Chandrashekhara_Kambara",
            "Chandrashekhar_Kambara"
        ),
        "adikavi pampa" to listOf(
            "Pampa_(poet)",
            "Adikavi_Pampa",
            "Pampa"
        ),
        "ranna" to listOf(
            "Ranna",
            "Ranna_(poet)"
        ),
        "basavanna" to listOf(
            "Basavanna",
            "Basava",
            "Basaveshwara"
        ),
        "akka mahadevi" to listOf(
            "Akka_Mahadevi",
            "Akkamahadevi"
        )
    )

    suspend fun fetchPoetImageUrl(poetName: String): String? = withContext(Dispatchers.IO) {
        val key = poetName.trim().lowercase()
        val namesToTry = poetWikipediaNames[key] ?: listOf(poetName.trim().replace(" ", "_"))

        Log.d(TAG, "Looking up: '$poetName' → trying ${namesToTry.size} names")

        for (name in namesToTry) {
            val result = fetchImageForName(name)
            if (result != null) {
                Log.d(TAG, "✅ '$poetName' found via '$name' → $result")
                return@withContext result
            }
            Log.d(TAG, "⚠️ No image for '$name'")
        }

        Log.d(TAG, "❌ All attempts failed for '$poetName'")
        null
    }

    private fun fetchImageForName(name: String): String? {
        return try {
            // Use underscore format — Wikipedia API works best this way
            val titleFormatted = name.replace(" ", "_")
            val apiUrl = "https://en.wikipedia.org/w/api.php" +
                    "?action=query" +
                    "&titles=$titleFormatted" +
                    "&prop=pageimages" +
                    "&format=json" +
                    "&pithumbsize=500"

            Log.d(TAG, "Fetching: $apiUrl")

            val connection = URL(apiUrl).openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty(
                    "User-Agent",
                    "KavyaKanajaApp/1.0 (Android; kavyakanaja@example.com)"
                )
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP $responseCode for '$name'")
                return null
            }

            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            val pages = json.getJSONObject("query").getJSONObject("pages")
            val pageKey = pages.keys().next()

            if (pageKey == "-1") {
                Log.d(TAG, "Page not found for '$name'")
                return null
            }

            val page = pages.getJSONObject(pageKey)
            if (page.has("thumbnail")) {
                val url = page.getJSONObject("thumbnail").getString("source")
                Log.d(TAG, "Found image for '$name': $url")
                url
            } else {
                Log.d(TAG, "No thumbnail for '$name'")
                null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Exception for '$name': ${e.message}")
            null
        }
    }
}