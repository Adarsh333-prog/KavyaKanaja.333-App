package com.app.kavyakanaja.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.app.kavyakanaja.KavyaTheme
import com.app.kavyakanaja.bhavartha.BhavarthaScreen
import com.app.kavyakanaja.listen.ListenScreen
import com.app.kavyakanaja.search.SearchScreen
import com.app.kavyakanaja.ui.home.FavoritesScreen
import com.app.kavyakanaja.ui.home.HomeScreen
import com.app.kavyakanaja.ui.home.PoemViewModel
import com.app.kavyakanaja.ui.home.PoetsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    vm: PoemViewModel,
    theme: KavyaTheme
) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // HOME
        composable("home") {

            HomeScreen(
                poemViewModel = vm,
                theme = theme,
                navController = navController
            )
        }

        // LISTEN
        composable("listen") {

            ListenScreen(
                poemViewModel = vm,
                theme = theme
            )
        }

        // BHAVARTHA
        composable("bhavartha") {

            BhavarthaScreen(
                poemViewModel = vm,
                theme = theme
            )
        }

        // POETS
        composable("poets") {

            PoetsScreen(
                poemViewModel = vm,
                theme = theme
            )
        }

        // SEARCH
        composable("search") {

            SearchScreen(
                poemViewModel = vm,
                theme = theme
            )
        }

        // FAVORITES
        composable("favorites") {

            FavoritesScreen(
                poemViewModel = vm,
                theme = theme
            )
        }

        // EDIT PROFILE
        composable("edit_profile") {

            EditProfileScreen(
                theme = theme,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // AI CHAT
        composable("ai_chat") {

            AiChatScreen(
                theme = theme
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    theme: KavyaTheme,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences(
        "user_profile",
        Context.MODE_PRIVATE
    )

    // SAVED NAME
    var name by remember {

        mutableStateOf(
            sharedPref.getString(
                "user_name",
                ""
            ) ?: ""
        )
    }

    // SAVED IMAGE
    var imageUri by remember {

        mutableStateOf(
            sharedPref.getString(
                "profile_image",
                null
            )
        )
    }

    // IMAGE PICKER
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        uri?.let {

            // PERMISSION
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            // SAVE IMAGE
            imageUri = it.toString()

            sharedPref.edit()
                .putString(
                    "profile_image",
                    it.toString()
                )
                .apply()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Edit Profile",

                fontSize = 22.sp,

                color = theme.textPrimary
            )

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.Close,

                    contentDescription = null,

                    tint = theme.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // PROFILE IMAGE
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    theme.accent.copy(alpha = 0.1f)
                )
                .clickable {

                    launcher.launch(
                        arrayOf("image/*")
                    )
                },

            contentAlignment = Alignment.Center
        ) {

            if (imageUri != null) {

                AsyncImage(
                    model = imageUri,

                    contentDescription = null,

                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )

            } else {

                Text(
                    text = "Add Photo",

                    color = theme.accent
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tap image to change photo",

            fontSize = 12.sp,

            color = theme.textSecondary
        )

        Spacer(modifier = Modifier.height(30.dp))

        // NAME FIELD
        OutlinedTextField(
            value = name,

            onValueChange = {
                name = it
            },

            label = {
                Text("Your Name")
            },

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(12.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.accent,
                unfocusedBorderColor = theme.border
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SAVE BUTTON
        Button(
            onClick = {

                // SAVE NAME
                sharedPref.edit()
                    .putString(
                        "user_name",
                        name
                    )
                    .apply()

                onBack()
            },

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(
                containerColor = theme.accent
            ),

            shape = RoundedCornerShape(12.dp)
        ) {

            Text(
                text = "Save",

                color = Color.White
            )
        }
    }
}