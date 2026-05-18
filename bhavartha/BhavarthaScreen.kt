package com.app.kavyakanaja.bhavartha

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kavyakanaja.KavyaTheme
import com.app.kavyakanaja.data.repository.GeminiHelper
import com.app.kavyakanaja.ui.home.PoemViewModel
import kotlinx.coroutines.launch

@Composable
fun BhavarthaScreen(poemViewModel: PoemViewModel, theme: KavyaTheme) {
    val todaysPoem by poemViewModel.todaysPoem.collectAsState()
    val scope = rememberCoroutineScope()

    var aiExplanation by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showAiCard by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            "ಭಾವಾರ್ಥ",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )
        Text(
            "Meaning & Explanation",
            fontSize = 14.sp,
            color = theme.textSecondary
        )

        Spacer(Modifier.height(20.dp))

        todaysPoem?.let { poem ->

            // Poem title
            Text(
                poem.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = theme.accent
            )
            Text(
                "ಇಂದಿನ ಕವಿತೆ",
                fontSize = 12.sp,
                color = theme.textSecondary,
                fontStyle = FontStyle.Italic
            )

            Spacer(Modifier.height(16.dp))

            // Kannada poem text card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = BorderStroke(0.5.dp, theme.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ಮೂಲ ಕವಿತೆ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    poem.kannadaText.split("\n").forEach { line ->
                        Text(
                            line,
                            fontSize = 17.sp,
                            color = theme.textPrimary,
                            lineHeight = 26.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // English Meaning card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = BorderStroke(0.5.dp, theme.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "English Meaning",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    poem.englishMeaning.split("\n").forEach { line ->
                        Text(
                            line,
                            fontSize = 15.sp,
                            color = theme.textSecondary,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bhavartha card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = theme.surface),
                border = BorderStroke(0.5.dp, theme.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ಭಾವಾರ್ಥ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accent
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        poem.bhavartha,
                        fontSize = 15.sp,
                        color = theme.textPrimary,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Ask AI Button
            Button(
                onClick = {
                    if (!isLoading) {
                        isLoading = true
                        showAiCard = true
                        aiExplanation = null
                        scope.launch {
                            aiExplanation = GeminiHelper.explainPoem(
                                kannadaText = poem.kannadaText,
                                englishMeaning = poem.englishMeaning,
                                bhavartha = poem.bhavartha
                            )
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.accent)
            ) {
                Text(
                    text = if (isLoading) "Getting AI Explanation..." else "🤖 Ask AI to Explain",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // AI Explanation card — appears after button click
            if (showAiCard) {
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = theme.accent.copy(alpha = 0.07f)
                    ),
                    border = BorderStroke(0.5.dp, theme.accent.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "🤖 AI Explanation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.accent
                        )
                        Spacer(Modifier.height(10.dp))
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CircularProgressIndicator(
                                    color = theme.accent,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Generating explanation...",
                                    color = theme.textSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            Text(
                                aiExplanation ?: "Could not generate explanation.",
                                fontSize = 14.sp,
                                color = theme.textPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}