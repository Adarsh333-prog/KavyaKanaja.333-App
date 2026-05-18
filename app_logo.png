package com.app.kavyakanaja.data.model

import com.google.gson.annotations.SerializedName

data class Poem(
    val id: Int = 0,
    val title: String = "",
    val category: String = "general",
    @SerializedName("kannada_text") val kannadaText: String = "",
    @SerializedName("english_meaning") val englishMeaning: String = "",
    val bhavartha: String = "",
    @SerializedName("poet_id") val poetId: Int = 0,
    @SerializedName("date_index") val dateIndex: Int = 0,
    @SerializedName("audio_url") val audioUrl: String = "",
    val words: Map<String, String> = emptyMap()
)

data class Poet(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("kannada_name") val kannadaName: String = "",
    val born: Int = 0,
    val died: Int? = null,
    val birthplace: String = "",
    @SerializedName("award_year") val awardYear: Int = 0,
    @SerializedName("famous_works") val famousWorks: String = "",
    val bio: String = ""
)

data class PoemData(
    val poems: List<Poem> = emptyList(),
    val poets: List<Poet> = emptyList()
)

