package com.app.kavyakanaja.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.kavyakanaja.KavyaTheme

@Composable
fun PoetsScreen(poemViewModel: PoemViewModel, theme: KavyaTheme) {
    val poets by poemViewModel.poets.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ಜ್ಞಾನಪೀಠ ಕವಿಗಳು", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = theme.textPrimary)
            Text("Kannada Jnanpith Awardees", fontSize = 14.sp, color = theme.textSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = theme.border.copy(alpha = 0.5f))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(poets) { poet ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = theme.surface),
                    border = BorderStroke(1.dp, theme.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(poet.kannadaName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = theme.accent)
                        Text("${poet.born} - ${poet.died ?: "Present"}", fontSize = 13.sp, color = theme.textSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Parameter name fixed below: lineWay -> lineHeight
                        Text(poet.bio, fontSize = 14.sp, color = theme.textPrimary, lineHeight = 20.sp)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}