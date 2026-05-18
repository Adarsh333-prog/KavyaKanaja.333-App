package com.app.kavyakanaja.search

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.kavyakanaja.KavyaTheme

@Composable
fun AppDrawerContent(
    userName: String,
    theme: KavyaTheme,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences(
        "user_profile",
        Context.MODE_PRIVATE
    )

    val savedName = sharedPref.getString("user_name", "User")
    val savedImage = sharedPref.getString("profile_image", null)

    ModalDrawerSheet(
        drawerContainerColor = theme.background,
        modifier = Modifier.width(300.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxHeight()
        ) {

            // HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(theme.surface)
                    .padding(24.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(theme.accent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {

                    if (savedImage != null) {
                        AsyncImage(
                            model = savedImage,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = theme.accent
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = savedName ?: "User",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = theme.textPrimary
                )

                Spacer(Modifier.height(6.dp))

                Surface(
                    modifier = Modifier.clickable {
                        onNavigate("edit_profile")
                        onCloseDrawer()
                    },
                    color = theme.accent.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {

                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Edit,
                            null,
                            Modifier.size(14.dp),
                            tint = theme.accent
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            "Edit Profile",
                            fontSize = 12.sp,
                            color = theme.accent
                        )
                    }
                }
            }

            HorizontalDivider(
                color = theme.border.copy(alpha = 0.3f)
            )

            Spacer(Modifier.height(8.dp))

            // FAVORITES
            NavigationDrawerItem(
                label = {
                    Text(
                        "❤️  My Favorites",
                        fontWeight = FontWeight.Medium,
                        color = theme.textPrimary
                    )
                },
                selected = false,
                onClick = {
                    onNavigate("favorites")
                    onCloseDrawer()
                },
                icon = {
                    Icon(Icons.Default.Favorite, null, tint = theme.accent)
                },
                badge = {
                    Text("All", fontSize = 11.sp, color = theme.accent)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ✅ SHARE APP ITEM (ADDED HERE)
            NavigationDrawerItem(
                label = {
                    Text(
                        "📲  Share App",
                        fontWeight = FontWeight.Medium,
                        color = theme.textPrimary
                    )
                },
                selected = false,
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Kavya Kanaja - Kannada Poetry App"
                        )
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "📖 Explore the beauty of Kannada literature with Kavya Kanaja!\n\n" +
                                    "Read poems, listen to recitations, and understand meanings.\n\n" +
                                    "Download here: https://play.google.com/store/apps/details?id=com.app.kavyakanaja"
                        )
                    }

                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share App")
                    )

                    onCloseDrawer()
                },
                icon = {
                    Icon(Icons.Default.Share, null, tint = theme.accent)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.weight(1f))

            // DARK MODE
            HorizontalDivider(
                color = theme.border.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector =
                            if (isDarkMode) Icons.Default.DarkMode
                            else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = theme.accent
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        if (isDarkMode) "Dark Mode" else "Light Mode",
                        color = theme.textPrimary,
                        fontSize = 15.sp
                    )
                }

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = theme.accent
                    )
                )
            }
        }
    }
}