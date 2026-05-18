package com.app.kavyakanaja.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kavyakanaja.KavyaTheme
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    theme: KavyaTheme
) {

    var message by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val messages = remember {
        mutableStateListOf<ChatMessage>()
    }

    val model = remember {

        GenerativeModel(
            modelName = "gemini-3-flash-preview",

            apiKey = "AIzaSyCrEub1z5cBYaRpjryG4VHawNx99lYe4Cs"
        )
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "🪶",
                            fontSize = 26.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Kavya Guru",
                            color = Color(0xFF5D4037),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F1E7))
                .padding(paddingValues)
        ) {

            // CHAT AREA
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),

                contentPadding = PaddingValues(16.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(messages) { chat ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            if (chat.isUser)
                                Arrangement.End
                            else
                                Arrangement.Start
                    ) {

                        Surface(
                            shape = RoundedCornerShape(18.dp),

                            color =
                                if (chat.isUser)
                                    Color(0xFF8D6E63)
                                else
                                    Color(0xFFEADBC8)
                        ) {

                            Text(
                                text = chat.text,

                                modifier = Modifier.padding(14.dp),

                                color =
                                    if (chat.isUser)
                                        Color.White
                                    else
                                        Color(0xFF3E2723),

                                fontSize = 15.sp,

                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }

                // SIMPLE TYPING ANIMATION
                if (isLoading) {

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.Start
                        ) {

                            Surface(
                                shape = RoundedCornerShape(18.dp),

                                color = Color(0xFFEADBC8)
                            ) {

                                Text(
                                    text = "AI is typing...",

                                    modifier = Modifier.padding(14.dp),

                                    color = Color(0xFF5D4037),

                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }
                    }
                }
            }

            // INPUT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = message,

                    onValueChange = {
                        message = it
                    },

                    modifier = Modifier.weight(1f),

                    placeholder = {
                        Text("Ask Kavya Guru...")
                    },

                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {

                        if (message.isNotBlank()) {

                            val userMessage = message

                            messages.add(
                                ChatMessage(
                                    userMessage,
                                    true
                                )
                            )

                            message = ""

                            isLoading = true

                            scope.launch {

                                try {

                                    delay(1000)

                                    val response =
                                        model.generateContent(
                                            userMessage
                                        )

                                    messages.add(
                                        ChatMessage(
                                            response.text
                                                ?: "No response",
                                            false
                                        )
                                    )

                                } catch (e: Exception) {

                                    messages.add(
                                        ChatMessage(
                                            "Error: ${e.message}",
                                            false
                                        )
                                    )
                                }

                                isLoading = false
                            }
                        }
                    },

                    containerColor = Color(0xFF8D6E63)
                ) {

                    Icon(
                        Icons.Default.Send,

                        contentDescription = null,

                        tint = Color.White
                    )
                }
            }
        }
    }
}