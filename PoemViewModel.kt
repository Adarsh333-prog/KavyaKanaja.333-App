package com.app.kavyakanaja.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.kavyakanaja.data.model.Poem
import com.app.kavyakanaja.data.model.Poet
import com.app.kavyakanaja.data.repository.PoemRepository
import com.app.kavyakanaja.util.WikipediaImageFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PoemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PoemRepository(application)
    private val sharedPrefs = application.getSharedPreferences("kavya_prefs", Context.MODE_PRIVATE)

    private val _todaysPoem = MutableStateFlow<Poem?>(null)
    val todaysPoem: StateFlow<Poem?> = _todaysPoem

    private val _poets = MutableStateFlow<List<Poet>>(emptyList())
    val poets: StateFlow<List<Poet>> = _poets

    private val _allPoems = MutableStateFlow<List<Poem>>(emptyList())
    val allPoems: StateFlow<List<Poem>> = _allPoems

    private val _poetImages = MutableStateFlow<Map<Int, String>>(emptyMap())
    val poetImages: StateFlow<Map<Int, String>> = _poetImages.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(loadSet("fav_ids"))
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _likedIds = MutableStateFlow<Set<Int>>(loadSet("liked_ids"))
    val likedIds: StateFlow<Set<Int>> = _likedIds.asStateFlow()

    private val _dislikedIds = MutableStateFlow<Set<Int>>(loadSet("disliked_ids"))
    val dislikedIds: StateFlow<Set<Int>> = _dislikedIds.asStateFlow()

    val favoritePoems: StateFlow<List<Poem>> = _allPoems.combine(_favoriteIds) { poems, ids ->
        poems.filter { it.id in ids }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            val fetchedPoets = repository.getAllPoets()
            _poets.value = fetchedPoets
            _allPoems.value = repository.getAllPoems()
            _todaysPoem.value = repository.getTodaysPoem()
            fetchWikiPhotos(fetchedPoets)
        }
    }

    private fun fetchWikiPhotos(poetList: List<Poet>) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageMap = mutableMapOf<Int, String>()
            poetList.forEach { poet ->
                val url = WikipediaImageFetcher.fetchPoetImageUrl(poet.name)
                if (url != null) imageMap[poet.id] = url
            }
            _poetImages.value = imageMap
        }
    }

    fun toggleFavorite(poemId: Int) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(poemId)) current.remove(poemId) else current.add(poemId)
        _favoriteIds.value = current
        saveSet("fav_ids", current)
    }

    fun toggleLike(poemId: Int) {
        val liked = _likedIds.value.toMutableSet()
        val disliked = _dislikedIds.value.toMutableSet()
        if (liked.contains(poemId)) liked.remove(poemId)
        else { liked.add(poemId); disliked.remove(poemId) }
        _likedIds.value = liked
        _dislikedIds.value = disliked
        saveSet("liked_ids", liked)
        saveSet("disliked_ids", disliked)
    }

    fun toggleDislike(poemId: Int) {
        val liked = _likedIds.value.toMutableSet()
        val disliked = _dislikedIds.value.toMutableSet()
        if (disliked.contains(poemId)) disliked.remove(poemId)
        else { disliked.add(poemId); liked.remove(poemId) }
        _likedIds.value = liked
        _dislikedIds.value = disliked
        saveSet("liked_ids", liked)
        saveSet("disliked_ids", disliked)
    }

    fun getPoetForPoem(poetId: Int): Poet? = _poets.value.find { it.id == poetId }

    private fun loadSet(key: String): Set<Int> =
        sharedPrefs.getStringSet(key, emptySet())
            ?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()

    private fun saveSet(key: String, set: Set<Int>) =
        sharedPrefs.edit().putStringSet(key, set.map { it.toString() }.toSet()).apply()
}