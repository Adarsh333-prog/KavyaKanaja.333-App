package com.app.kavyakanaja.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class `DictionaryRepository`(private val context: Context) {

    fun getDictionary(): Map<String, String> {

        val json = context.assets.open("dictionary.json")
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<Map<String, String>>() {}.type

        return Gson().fromJson(json, type)
    }
}