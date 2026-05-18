package com.app.kavyakanaja.search

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.app.kavyakanaja.KavyaTheme
import com.app.kavyakanaja.data.model.Poem
import com.app.kavyakanaja.data.model.Poet
import com.app.kavyakanaja.ui.home.PoemViewModel
import java.util.Locale

data class PoemCategory(
    val id: String,
    val emoji: String,
    val label: String,
    val kannada: String
)

val poemCategories = listOf(
    PoemCategory("all", "📚", "All", "ಎಲ್ಲ"),
    PoemCategory("nature", "🌿", "Nature", "ಪ್ರಕೃತಿ"),
    PoemCategory("love", "❤️", "Love", "ಪ್ರೇಮ"),
    PoemCategory("mother", "👩", "Mother", "ತಾಯಿ"),
    PoemCategory("devotion", "🙏", "Devotion", "ಭಕ್ತಿ"),
    PoemCategory("patriotism", "🌍", "Patriotism", "ದೇಶಭಕ್ತಿ"),
    PoemCategory("philosophy", "📖", "Philosophy", "ತತ್ವ"),
    PoemCategory("social", "✊", "Social", "ಸಾಮಾಜಿಕ")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(poemViewModel: PoemViewModel, theme: KavyaTheme) {
    val poems       by poemViewModel.allPoems.collectAsState()
    val poets       by poemViewModel.poets.collectAsState()
    val favoriteIds by poemViewModel.favoriteIds.collectAsState()
    val likedIds    by poemViewModel.likedIds.collectAsState()
    val dislikedIds by poemViewModel.dislikedIds.collectAsState()

    var searchQuery      by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    var selectedPoet     by remember { mutableStateOf<Poet?>(null) }
    var selectedPoem     by remember { mutableStateOf<Poem?>(null) }

    val searchFilteredPoets = remember(searchQuery, poets) {
        if (searchQuery.length < 2) emptyList()
        else poets.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.kannadaName.contains(searchQuery)
        }
    }

    val searchFilteredPoems = remember(searchQuery, poems) {
        if (searchQuery.length < 2) emptyList()
        else poems.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.kannadaText.contains(searchQuery)
        }
    }

    val categoryFilteredPoems = remember(selectedCategory, poems) {
        if (selectedCategory == "all") poems
        else poems.filter { it.category == selectedCategory }
    }

    val isSearching = searchQuery.length >= 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "ಹುಡುಕಿ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary,
                fontFamily = FontFamily.Serif
            )
            Text(
                "Search poems and poets",
                fontSize = 13.sp,
                color = theme.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "ಕವಿ ಅಥವಾ ಕವಿತೆಯ ಹೆಸರು...",
                        color = theme.textSecondary
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = theme.accent)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accent,
                    unfocusedBorderColor = theme.border,
                    focusedContainerColor = theme.surface,
                    unfocusedContainerColor = theme.surface
                )
            )
        }

        if (!isSearching) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(poemCategories) { category ->
                    val isSelected = selectedCategory == category.id
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) theme.accent else theme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) theme.accent else theme.border
                        ),
                        modifier = Modifier.clickable {
                            selectedCategory = category.id
                        }
                    ) {
                        Text(
                            "${category.emoji} ${category.label}",
                            modifier = Modifier.padding(
                                horizontal = 14.dp, vertical = 8.dp
                            ),
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White
                            else theme.textPrimary
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isSearching) {
                if (searchFilteredPoets.isNotEmpty()) {
                    item {
                        Text(
                            "✍️ Poets",
                            fontWeight = FontWeight.Bold,
                            color = theme.accent,
                            fontSize = 13.sp
                        )
                    }
                    items(searchFilteredPoets) { poet ->
                        PoetCard(
                            poet = poet,
                            theme = theme,
                            onClick = { selectedPoet = poet }
                        )
                    }
                }
                if (searchFilteredPoems.isNotEmpty()) {
                    item {
                        Text(
                            "📖 Poems",
                            fontWeight = FontWeight.Bold,
                            color = theme.accent,
                            fontSize = 13.sp
                        )
                    }
                    items(searchFilteredPoems) { poem ->
                        val poet = poemViewModel.getPoetForPoem(poem.poetId)
                        PoemCard(
                            poem = poem,
                            poetName = poet?.name ?: "",
                            isFav = favoriteIds.contains(poem.id),
                            isLiked = likedIds.contains(poem.id),
                            isDisliked = dislikedIds.contains(poem.id),
                            theme = theme,
                            onFavClick = { poemViewModel.toggleFavorite(poem.id) },
                            onLikeClick = { poemViewModel.toggleLike(poem.id) },
                            onDislikeClick = { poemViewModel.toggleDislike(poem.id) },
                            onClick = { selectedPoem = poem }
                        )
                    }
                }
            } else {
                items(categoryFilteredPoems) { poem ->
                    val poet = poemViewModel.getPoetForPoem(poem.poetId)
                    PoemCard(
                        poem = poem,
                        poetName = poet?.name ?: "",
                        isFav = favoriteIds.contains(poem.id),
                        isLiked = likedIds.contains(poem.id),
                        isDisliked = dislikedIds.contains(poem.id),
                        theme = theme,
                        onFavClick = { poemViewModel.toggleFavorite(poem.id) },
                        onLikeClick = { poemViewModel.toggleLike(poem.id) },
                        onDislikeClick = { poemViewModel.toggleDislike(poem.id) },
                        onClick = { selectedPoem = poem }
                    )
                }
            }
        }
    }

    // Poet detail dialog
    if (selectedPoet != null) {
        PoetDetailDialog(
            poet = selectedPoet!!,
            viewModel = poemViewModel,
            theme = theme,
            onDismiss = { selectedPoet = null },
            onPoemClick = { poem ->
                selectedPoet = null
                selectedPoem = poem
            }
        )
    }

    // Poem detail dialog with TTS + Share
    selectedPoem?.let { poem ->
        val context = LocalContext.current
        val poetName = poemViewModel.getPoetForPoem(poem.poetId)?.name ?: ""

        // TTS state for this dialog
        var tts by remember { mutableStateOf<TextToSpeech?>(null) }
        var isPlayingKannada by remember { mutableStateOf(false) }
        var isPlayingEnglish by remember { mutableStateOf(false) }
        var isTtsReady by remember { mutableStateOf(false) }

        LaunchedEffect(poem.id) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    isTtsReady = true
                    tts?.setOnUtteranceProgressListener(
                        object : android.speech.tts.UtteranceProgressListener() {
                            override fun onStart(id: String?) {
                                isPlayingKannada = id == "kn"
                                isPlayingEnglish = id == "en"
                            }
                            override fun onDone(id: String?) {
                                isPlayingKannada = false
                                isPlayingEnglish = false
                            }
                            override fun onError(id: String?) {
                                isPlayingKannada = false
                                isPlayingEnglish = false
                            }
                        }
                    )
                }
            }
        }

        DisposableEffect(poem.id) {
            onDispose {
                tts?.stop()
                tts?.shutdown()
            }
        }

        Dialog(onDismissRequest = {
            tts?.stop()
            selectedPoem = null
        }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                color = theme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Title + Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            poem.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = theme.accent,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            tts?.stop()
                            selectedPoem = null
                        }) {
                            Icon(
                                Icons.Default.Close,
                                null,
                                tint = theme.textSecondary
                            )
                        }
                    }

                    Text(
                        "— $poetName",
                        fontSize = 12.sp,
                        color = theme.textSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = theme.border, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Kannada text card with play button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "ಮೂಲ ಪದ್ಯ",
                                fontSize = 11.sp,
                                color = theme.accent,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                poem.kannadaText,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                color = theme.textPrimary,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Small circle play button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (isPlayingKannada) "Playing..." else "ಕನ್ನಡ",
                                    fontSize = 11.sp,
                                    color = theme.textSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isPlayingKannada)
                                                Color(0xFF8B4513)
                                            else
                                                Color(0xFF5A3A10)
                                        )
                                        .clickable {
                                            if (!isTtsReady) return@clickable
                                            if (isPlayingKannada) {
                                                tts?.stop()
                                                isPlayingKannada = false
                                            } else {
                                                tts?.language = Locale("kn", "IN")
                                                tts?.speak(
                                                    poem.kannadaText,
                                                    TextToSpeech.QUEUE_FLUSH,
                                                    null,
                                                    "kn"
                                                )
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (isPlayingKannada) "⏹" else "▶",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // English meaning card with play button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5EE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "English Meaning",
                                fontSize = 11.sp,
                                color = Color(0xFF085041),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                poem.englishMeaning,
                                fontSize = 14.sp,
                                color = Color(0xFF1A3A2A),
                                lineHeight = 22.sp,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Small circle play button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (isPlayingEnglish) "Playing..." else "English",
                                    fontSize = 11.sp,
                                    color = Color(0xFF085041)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isPlayingEnglish)
                                                Color(0xFF085041)
                                            else
                                                Color(0xFF2E7D52)
                                        )
                                        .clickable {
                                            if (!isTtsReady) return@clickable
                                            if (isPlayingEnglish) {
                                                tts?.stop()
                                                isPlayingEnglish = false
                                            } else {
                                                tts?.language = Locale.ENGLISH
                                                tts?.speak(
                                                    poem.englishMeaning,
                                                    TextToSpeech.QUEUE_FLUSH,
                                                    null,
                                                    "en"
                                                )
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (isPlayingEnglish) "⏹" else "▶",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bhavartha
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "ಭಾವಾರ್ಥ",
                                fontSize = 11.sp,
                                color = theme.accent,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                poem.bhavartha,
                                fontSize = 14.sp,
                                color = theme.textPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons row — Share + Copy + Close
                    val shareText =
                        "${poem.title}\n\n${poem.kannadaText}\n\n— $poetName"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Share button
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(
                                    Intent.createChooser(intent, "Share Poem")
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = theme.accent
                            ),
                            border = BorderStroke(1.dp, theme.accent)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 13.sp)
                        }

                        // Copy button
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager
                                clipboard.setPrimaryClip(
                                    ClipData.newPlainText("poem", shareText)
                                )
                                Toast.makeText(
                                    context, "Poem copied!", Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = theme.accent
                            ),
                            border = BorderStroke(1.dp, theme.accent)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 13.sp)
                        }

                        // Close button
                        Button(
                            onClick = {
                                tts?.stop()
                                selectedPoem = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.accent
                            )
                        ) {
                            Text("Close", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoemCard(
    poem: Poem,
    poetName: String,
    isFav: Boolean,
    isLiked: Boolean,
    isDisliked: Boolean,
    theme: KavyaTheme,
    onFavClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(0.5.dp, theme.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        poem.title,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        "— $poetName",
                        fontSize = 11.sp,
                        color = theme.textSecondary
                    )
                }
                IconButton(onClick = onFavClick) {
                    Icon(
                        if (isFav) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (isFav) Color.Red else theme.textSecondary
                    )
                }
            }

            val cat = poemCategories.find { it.id == poem.category }
            if (cat != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = theme.accent.copy(alpha = 0.1f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        "${cat.emoji} ${cat.label}",
                        Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = theme.accent
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        if (isLiked) Icons.Default.ThumbUp
                        else Icons.Outlined.ThumbUp,
                        "Like",
                        Modifier.size(18.dp),
                        tint = if (isLiked) theme.accent else theme.textSecondary
                    )
                }
                IconButton(onClick = onDislikeClick) {
                    Icon(
                        if (isDisliked) Icons.Default.ThumbDown
                        else Icons.Outlined.ThumbDown,
                        "Dislike",
                        Modifier.size(18.dp),
                        tint = if (isDisliked) Color.Red else theme.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun PoetCard(poet: Poet, theme: KavyaTheme, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(theme.surface),
        border = BorderStroke(0.5.dp, theme.border)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(theme.accent.copy(0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    poet.kannadaName.first().toString(),
                    color = theme.accent,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    poet.kannadaName,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
                Text(
                    poet.name,
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }
        }
    }
}

@Composable
fun PoetDetailDialog(
    poet: Poet,
    viewModel: PoemViewModel,
    theme: KavyaTheme,
    onDismiss: () -> Unit,
    onPoemClick: (Poem) -> Unit
) {
    val wikiImages by viewModel.poetImages.collectAsState()
    val allPoems   by viewModel.allPoems.collectAsState()
    val imageUrl   = wikiImages[poet.id]

    val topPoems = remember(poet, allPoems) {
        allPoems.filter { it.poetId == poet.id }.take(5)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(28.dp),
            color = theme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUrl != null) {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = poet.name,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop,
                        error = { PoetAvatar(poet, theme) }
                    )
                } else {
                    PoetAvatar(poet, theme)
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    poet.kannadaName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
                Text(
                    poet.name,
                    fontSize = 14.sp,
                    color = theme.textSecondary
                )

                HorizontalDivider(
                    Modifier.padding(vertical = 16.dp),
                    color = theme.border.copy(0.5f)
                )

                WikiRow("Born", "${poet.born}", theme)
                WikiRow("Place", poet.birthplace, theme)
                WikiRow("Works", poet.famousWorks, theme)

                Spacer(Modifier.height(16.dp))
                Text(
                    "About",
                    Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = theme.accent
                )
                Text(
                    poet.bio,
                    fontSize = 14.sp,
                    color = theme.textPrimary,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(20.dp))
                Text(
                    "Famous Poems",
                    Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = theme.accent
                )
                Spacer(Modifier.height(10.dp))

                topPoems.forEach { poem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onPoemClick(poem) },
                        colors = CardDefaults.cardColors(
                            containerColor = theme.surface
                        ),
                        border = BorderStroke(0.5.dp, theme.border)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                poem.title,
                                color = theme.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                poem.kannadaText.take(60) + "...",
                                color = theme.textSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.accent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun WikiRow(label: String, value: String, theme: KavyaTheme) {
    Row(
        Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            label,
            Modifier.width(80.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = theme.accent
        )
        Text(
            value,
            Modifier.weight(1f),
            fontSize = 13.sp,
            color = theme.textPrimary
        )
    }
}

@Composable
fun PoetAvatar(poet: Poet, theme: KavyaTheme) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(theme.accent.copy(0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            poet.kannadaName.first().toString(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = theme.accent
        )
    }
}