package com.app.kavyakanaja.listen

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.kavyakanaja.KavyaTheme
import com.app.kavyakanaja.ui.home.PoemViewModel
import java.util.Locale

@Composable
fun ListenScreen(poemViewModel: PoemViewModel, theme: KavyaTheme) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val todaysPoem by poemViewModel.todaysPoem.collectAsState()

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlayingKannada by remember { mutableStateOf(false) }
    var isPlayingEnglish by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) }
    var currentWordIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                tts?.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            isPlayingKannada = utteranceId == "kannada"
                            isPlayingEnglish = utteranceId == "english"
                        }
                        override fun onDone(utteranceId: String?) {
                            isPlayingKannada = false
                            isPlayingEnglish = false
                            currentWordIndex = -1
                        }
                        override fun onError(utteranceId: String?) {
                            isPlayingKannada = false
                            isPlayingEnglish = false
                            currentWordIndex = -1
                        }
                        override fun onRangeStart(
                            utteranceId: String?,
                            start: Int,
                            end: Int,
                            frame: Int
                        ) {
                            if (utteranceId == "kannada") {
                                val fullText = todaysPoem?.kannadaText ?: ""
                                val words = fullText
                                    .split(" ", "\n")
                                    .filter { it.isNotEmpty() }
                                var charCount = 0
                                for (i in words.indices) {
                                    charCount += words[i].length
                                    if (charCount >= start) {
                                        currentWordIndex = i
                                        break
                                    }
                                    charCount++
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                tts?.stop()
                isPlayingKannada = false
                isPlayingEnglish = false
                currentWordIndex = -1
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🎵 ಕೇಳಿ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )
        Text(
            text = "Listen to today's poem",
            fontSize = 13.sp,
            color = theme.textSecondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (todaysPoem != null) {
            val poem = todaysPoem!!

            Text(
                text = poem.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = theme.accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // ── Kannada card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = theme.surface
                ),
                border = BorderStroke(0.5.dp, theme.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Poem lines with word highlight
                    var wordCounter = 0
                    poem.kannadaText.split("\n").forEach { line ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            line.split(" ").filter { it.isNotEmpty() }
                                .forEach { word ->
                                    val thisWordIndex = wordCounter
                                    wordCounter++
                                    val isCurrentWord = isPlayingKannada &&
                                            thisWordIndex == currentWordIndex &&
                                            currentWordIndex != -1
                                    Text(
                                        text = "$word ",
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = if (isCurrentWord)
                                            Color(0xFFFF6B00)
                                        else
                                            theme.textPrimary,
                                        fontWeight = if (isCurrentWord)
                                            FontWeight.Bold
                                        else
                                            FontWeight.Normal,
                                        textDecoration = if (isCurrentWord)
                                            TextDecoration.Underline
                                        else
                                            TextDecoration.None,
                                        lineHeight = 28.sp
                                    )
                                }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = theme.border, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Small play button aligned to right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (isPlayingKannada) {
                                    tts?.stop()
                                    isPlayingKannada = false
                                    currentWordIndex = -1
                                } else {
                                    if (isTtsReady) {
                                        tts?.language = Locale("kn", "IN")
                                        tts?.speak(
                                            poem.kannadaText,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "kannada"
                                        )
                                    }
                                }
                            },
                            enabled = isTtsReady,
                            modifier = Modifier
                                .width(120.dp)
                                .height(36.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingKannada)
                                    Color(0xFF8B4513)
                                else
                                    Color(0xFF5A3A10)
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp, vertical = 0.dp
                            )
                        ) {
                            Text(
                                text = if (isPlayingKannada) "⏹ Stop" else "▶ Play",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── English card ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5EE)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "English Meaning",
                        fontSize = 12.sp,
                        color = Color(0xFF085041),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    poem.englishMeaning.split("\n").forEach { line ->
                        Text(
                            text = line,
                            fontSize = 14.sp,
                            color = Color(0xFF1A3A2A),
                            lineHeight = 22.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        color = Color(0xFF9FE1CB),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Small play button aligned to right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (isPlayingEnglish) {
                                    tts?.stop()
                                    isPlayingEnglish = false
                                } else {
                                    if (isTtsReady) {
                                        tts?.language = Locale.ENGLISH
                                        tts?.speak(
                                            poem.englishMeaning,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "english"
                                        )
                                    }
                                }
                            },
                            enabled = isTtsReady,
                            modifier = Modifier
                                .width(120.dp)
                                .height(36.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingEnglish)
                                    Color(0xFF085041)
                                else
                                    Color(0xFF2E7D52)
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp, vertical = 0.dp
                            )
                        ) {
                            Text(
                                text = if (isPlayingEnglish) "⏹ Stop" else "▶ Play",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Speech Speed
            Text(
                text = "Speech Speed",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Slow" to 0.6f, "Normal" to 1.0f, "Fast" to 1.4f)
                    .forEach { (label, speed) ->
                        OutlinedButton(
                            onClick = { tts?.setSpeechRate(speed) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = theme.accent
                            ),
                            border = BorderStroke(1.dp, theme.accent)
                        ) {
                            Text(
                                label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(24.dp))

        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = theme.accent)
            }
        }
    }
}