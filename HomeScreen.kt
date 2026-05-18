package com.app.kavyakanaja.ui.home
import androidx.compose.foundation.Image
import com.app.kavyakanaja.R
import androidx.compose.ui.res.painterResource
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.kavyakanaja.KavyaTheme
import com.app.kavyakanaja.data.repository.GeminiHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    poemViewModel: PoemViewModel,
    theme: KavyaTheme,
    navController: NavController
) {

    val context = LocalContext.current

    val todaysPoem by poemViewModel.todaysPoem.collectAsState()
    val favoriteIds by poemViewModel.favoriteIds.collectAsState()
    val likedIds by poemViewModel.likedIds.collectAsState()
    val dislikedIds by poemViewModel.dislikedIds.collectAsState()

    var selectedWord by remember { mutableStateOf<String?>(null) }
    var selectedMeaning by remember { mutableStateOf<String?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    var isMeaningLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val wordMeaningCache = remember {
        mutableMapOf<String, String>()
    }

    // WORD MEANING SHEET
    if (showSheet && selectedWord != null) {

        ModalBottomSheet(
            onDismissRequest = {

                showSheet = false
                selectedWord = null
                selectedMeaning = null
            },

            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp
            ),

            containerColor = theme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 48.dp
                    ),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = selectedWord!!,

                    fontSize = 28.sp,

                    fontFamily = FontFamily.Serif,

                    fontWeight = FontWeight.Bold,

                    color = theme.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),

                    color = theme.surface,

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier.padding(16.dp),

                        contentAlignment = Alignment.Center
                    ) {

                        if (isMeaningLoading) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,

                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),

                                    color = theme.accent,

                                    strokeWidth = 2.dp
                                )

                                Text(
                                    text = "Getting meaning...",

                                    fontSize = 13.sp,

                                    color = theme.textSecondary
                                )
                            }

                        } else {

                            Text(
                                text = selectedMeaning ?: "",

                                fontSize = 15.sp,

                                color = theme.textSecondary,

                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {

                        showSheet = false
                        selectedWord = null
                        selectedMeaning = null
                    }
                ) {

                    Text(
                        "Close",
                        color = theme.accent
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // MAIN SCREEN
    Scaffold(

        floatingActionButton = {

            FloatingActionButton(
                onClick = {

                    navController.navigate("ai_chat")
                },

                shape = RoundedCornerShape(100.dp),

                containerColor = Color.Transparent,

                elevation = FloatingActionButtonDefaults.elevation(0.dp)

            ) {

                Image(
                    painter = painterResource(id = R.drawable.plume),
                    contentDescription = "AI",
                    modifier = Modifier.size(56.dp)
                )
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ಕಾವ್ಯ ಕಣಜ",

                fontSize = 32.sp,

                fontWeight = FontWeight.Bold,

                color = theme.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Poem of the Day",

                fontSize = 14.sp,

                color = theme.textSecondary.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (todaysPoem != null) {

                val poem = todaysPoem!!

                val poet =
                    poemViewModel.getPoetForPoem(poem.poetId)

                val isFav =
                    favoriteIds.contains(poem.id)

                val isLiked =
                    likedIds.contains(poem.id)

                val isDisliked =
                    dislikedIds.contains(poem.id)

                // POET TAG
                Surface(
                    shape = RoundedCornerShape(100.dp),

                    color = theme.surface
                ) {

                    Text(
                        text =
                            "— ${poet?.name ?: "Unknown"}  •  ${
                                if ((poet?.awardYear ?: 0) > 0)
                                    "Jnanpith ${poet?.awardYear}"
                                else
                                    "Classic"
                            }",

                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 4.dp
                        ),

                        fontSize = 11.sp,

                        color = theme.accent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "💡 Tap any word to see its meaning",

                    fontSize = 11.sp,

                    color = theme.accent.copy(alpha = 0.8f),

                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(12.dp))

                // POEM CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = theme.background
                    ),

                    border = BorderStroke(
                        0.5.dp,
                        theme.border
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        // TITLE
                        Row(
                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text = poem.title,

                                fontSize = 18.sp,

                                fontWeight = FontWeight.Bold,

                                color = theme.accent,

                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {

                                    poemViewModel.toggleFavorite(
                                        poem.id
                                    )
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (isFav)
                                            Icons.Default.Favorite
                                        else
                                            Icons.Default.FavoriteBorder,

                                    contentDescription = "Favourite",

                                    tint =
                                        if (isFav)
                                            Color.Red
                                        else
                                            theme.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // POEM LINES
                        poem.kannadaText
                            .split("\n")
                            .forEach { line ->

                                Row(
                                    modifier = Modifier
                                        .padding(vertical = 3.dp)
                                        .fillMaxWidth(),

                                    horizontalArrangement =
                                        Arrangement.Start
                                ) {

                                    line.split(" ")
                                        .filter {
                                            it.isNotEmpty()
                                        }
                                        .forEach { word ->

                                            val cleanWord =
                                                word.replace(
                                                    Regex("[.,!?;:]"),
                                                    ""
                                                )

                                            val hasMeaning =
                                                poem.words.containsKey(
                                                    cleanWord
                                                )

                                            Text(
                                                text = "$word ",

                                                fontSize = 20.sp,

                                                fontFamily =
                                                    FontFamily.Serif,

                                                color =
                                                    if (hasMeaning)
                                                        theme.accent
                                                    else
                                                        theme.textPrimary,

                                                fontWeight =
                                                    if (hasMeaning)
                                                        FontWeight.Medium
                                                    else
                                                        FontWeight.Normal,

                                                textDecoration =
                                                    if (hasMeaning)
                                                        TextDecoration.Underline
                                                    else
                                                        TextDecoration.None,

                                                modifier =
                                                    Modifier.clickable {

                                                        selectedWord =
                                                            cleanWord

                                                        showSheet = true

                                                        val jsonMeaning =
                                                            poem.words[cleanWord]

                                                        val cachedMeaning =
                                                            wordMeaningCache[cleanWord]

                                                        when {

                                                            jsonMeaning != null -> {

                                                                selectedMeaning =
                                                                    jsonMeaning

                                                                isMeaningLoading =
                                                                    false
                                                            }

                                                            cachedMeaning != null -> {

                                                                selectedMeaning =
                                                                    cachedMeaning

                                                                isMeaningLoading =
                                                                    false
                                                            }

                                                            else -> {

                                                                isMeaningLoading =
                                                                    true

                                                                selectedMeaning =
                                                                    null

                                                                scope.launch {

                                                                    val meaning =
                                                                        GeminiHelper.getWordMeaning(
                                                                            word = cleanWord,
                                                                            poemContext =
                                                                                poem.kannadaText
                                                                        )

                                                                    wordMeaningCache[cleanWord] =
                                                                        meaning

                                                                    selectedMeaning =
                                                                        meaning

                                                                    isMeaningLoading =
                                                                        false
                                                                }
                                                            }
                                                        }
                                                    }
                                            )
                                        }
                                }
                            }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            color = theme.border,
                            thickness = 0.5.dp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "— ${poet?.name ?: ""}",

                            fontSize = 13.sp,

                            fontWeight = FontWeight.Medium,

                            color = theme.textSecondary
                        )

                        if ((poet?.awardYear ?: 0) > 0) {

                            Text(
                                text =
                                    "Jnanpith Award ${poet?.awardYear}",

                                fontSize = 11.sp,

                                color = theme.accent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // LIKE CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = theme.surface
                    ),

                    border = BorderStroke(
                        0.5.dp,
                        theme.border
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),

                        horizontalArrangement =
                            Arrangement.SpaceEvenly,

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Did you like this poem?",

                            fontSize = 13.sp,

                            color = theme.textSecondary,

                            modifier = Modifier.weight(1f)
                        )

                        // SHARE
                        IconButton(
                            onClick = {

                                val shareIntent = Intent().apply {

                                    action = Intent.ACTION_SEND

                                    putExtra(
                                        Intent.EXTRA_TEXT,

                                        "${poem.title}\n\n${poem.kannadaText}\n\nhttps://kavyakanaja.app/poem/${poem.id}\n\nShared from KavyaKanaja App"
                                    )

                                    type = "text/plain"
                                }

                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share Poem"
                                    )
                                )
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Default.Share,

                                contentDescription = "Share",

                                tint = theme.accent,

                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // LIKE
                        IconButton(
                            onClick = {

                                poemViewModel.toggleLike(
                                    poem.id
                                )
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (isLiked)
                                        Icons.Default.ThumbUp
                                    else
                                        Icons.Outlined.ThumbUp,

                                contentDescription = "Like",

                                tint =
                                    if (isLiked)
                                        Color(0xFF2E7D52)
                                    else
                                        theme.textSecondary,

                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // DISLIKE
                        IconButton(
                            onClick = {

                                poemViewModel.toggleDislike(
                                    poem.id
                                )
                            }
                        ) {

                            Icon(
                                imageVector =
                                    if (isDisliked)
                                        Icons.Default.ThumbDown
                                    else
                                        Icons.Outlined.ThumbDown,

                                contentDescription = "Dislike",

                                tint =
                                    if (isDisliked)
                                        Color.Red
                                    else
                                        theme.textSecondary,

                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

            } else {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    CircularProgressIndicator(
                        color = theme.accent
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Loading poem...",
                        color = theme.textSecondary
                    )
                }
            }
        }
    }
}