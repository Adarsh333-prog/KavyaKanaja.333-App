package com.app.kavyakanaja.data.repository

import android.content.Context
import com.app.kavyakanaja.data.model.Poem
import com.app.kavyakanaja.data.model.PoemData
import com.app.kavyakanaja.data.model.Poet
import com.google.gson.Gson
import java.time.LocalDate

class PoemRepository(private val context: Context) {

    private fun loadData(): PoemData {
        val json = context.assets
            .open("poems.json")
            .bufferedReader()
            .use { it.readText() }
        return Gson().fromJson(json, PoemData::class.java)
    }

    fun getTodaysPoem(): Poem {
        val poems = loadData().poems
        val dayIndex = LocalDate.now().dayOfYear % poems.size
        return poems[dayIndex]
    }

    fun getAllPoems(): List<Poem> = loadData().poems

    fun getAllPoets(): List<Poet> = loadData().poets

    fun getPoetById(id: Int): Poet? =
        loadData().poets.find { it.id == id }
}