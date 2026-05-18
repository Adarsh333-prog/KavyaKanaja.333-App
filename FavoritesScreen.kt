package com.app.kavyakanaja.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.kavyakanaja.KavyaTheme
import com.app.kavyakanaja.data.model.Poem

@Composable
fun FavoritesScreen(poemViewModel: PoemViewModel, theme: KavyaTheme) {
    val favoritePoems  by poemViewModel.favoritePoems.collectAsState()
    val favoriteIds    by poemViewModel.favoriteIds.collectAsState()
    val likedIds       by poemViewModel.likedIds.collectAsState()
    val dislikedIds    by poemViewModel.dislikedIds.collectAsState()

    var selectedPoem by remember { mutableStateOf<Poem?>(null) }

    // Poem detail popup
    selectedPoem?.let { poem ->
        val poet = poemViewModel.getPoetForPoem(poem.poetId)
        Dialog(onDismissRequest = { selectedPoem = null }) {
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
                    // Title row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = poem.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = theme.accent,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedPoem = null }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = theme.textSecondary
                            )
                        }
                    }

                    // Poet name
                    Text(
                        text = "— ${poet?.name ?: ""}",
                        fontSize = 13.sp,
                        color = theme.textSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    HorizontalDivider(color = theme.border, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Kannada text
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ಮೂಲ ಪದ್ಯ",
                                fontSize = 11.sp,
                                color = theme.accent,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = poem.kannadaText,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Serif,
                                color = theme.textPrimary,
                                lineHeight = 28.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // English meaning
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5EE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "English Meaning",
                                fontSize = 11.sp,
                                color = Color(0xFF085041),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = poem.englishMeaning,
                                fontSize = 14.sp,
                                color = Color(0xFF1A3A2A),
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bhavartha
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = theme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ಭಾವಾರ್ಥ",
                                fontSize = 11.sp,
                                color = theme.accent,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = poem.bhavartha,
                                fontSize = 14.sp,
                                color = theme.textPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedPoem = null },
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "❤️ Favorites",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
            )
            Text(
                "Your saved poems",
                fontSize = 14.sp,
                color = theme.textSecondary
            )
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = theme.border.copy(alpha = 0.5f))
        }

        if (favoritePoems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No favorites yet",
                        fontSize = 16.sp,
                        color = theme.textSecondary
                    )
                    Text(
                        "Tap ❤️ on any poem to save it here",
                        fontSize = 13.sp,
                        color = theme.textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 16.dp, vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favoritePoems) { poem ->
                    val poet = poemViewModel.getPoetForPoem(poem.poetId)
                    FavPoemCard(
                        poem       = poem,
                        poetName   = poet?.name ?: "",
                        isFavorite = favoriteIds.contains(poem.id),
                        isLiked    = likedIds.contains(poem.id),
                        isDisliked = dislikedIds.contains(poem.id),
                        theme      = theme,
                        onFavClick     = { poemViewModel.toggleFavorite(poem.id) },
                        onLikeClick    = { poemViewModel.toggleLike(poem.id) },
                        onDislikeClick = { poemViewModel.toggleDislike(poem.id) },
                        onCardClick    = { selectedPoem = poem }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun FavPoemCard(
    poem: Poem,
    poetName: String,
    isFavorite: Boolean,
    isLiked: Boolean,
    isDisliked: Boolean,
    theme: KavyaTheme,
    onFavClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.surface),
        border = BorderStroke(0.5.dp, theme.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        poem.title,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontFamily = FontFamily.Serif,
                        fontSize = 15.sp
                    )
                    Text(
                        "— $poetName",
                        fontSize = 11.sp,
                        color = theme.textSecondary
                    )
                }
                IconButton(onClick = onFavClick) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (isFavorite) Color.Red
                        else theme.textSecondary
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Preview first line
            Text(
                text = poem.kannadaText.split("\n").first(),
                fontSize = 13.sp,
                fontFamily = FontFamily.Serif,
                color = theme.textSecondary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tap to read full poem →",
                fontSize = 11.sp,
                color = theme.accent
            )

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        if (isLiked) Icons.Default.ThumbUp
                        else Icons.Outlined.ThumbUp,
                        "Like",
                        Modifier.size(20.dp),
                        tint = if (isLiked) theme.accent
                        else theme.textSecondary
                    )
                }
                IconButton(onClick = onDislikeClick) {
                    Icon(
                        if (isDisliked) Icons.Default.ThumbDown
                        else Icons.Outlined.ThumbDown,
                        "Dislike",
                        Modifier.size(20.dp),
                        tint = if (isDisliked) Color.Red
                        else theme.textSecondary
                    )
                }
            }
        }
    }
}