package com.app.kavyakanaja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.kavyakanaja.search.AppDrawerContent
import com.app.kavyakanaja.ui.NavGraph
import com.app.kavyakanaja.ui.home.PoemViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { KavyaApp() }
    }
}

@Composable
fun KavyaApp() {
    val poemViewModel: PoemViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    // Always show splash first for everyone
    var appState by remember { mutableStateOf("splash") }

    when (appState) {
        "splash" -> SplashScreen(onSplashFinished = {
            // After splash — check if already logged in
            appState = if (auth.currentUser != null) "home" else "login"
        })
        "login" -> LoginScreen(onLoginSuccess = { appState = "home" })
        "home"  -> MainScreen(poemViewModel = poemViewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(poemViewModel: PoemViewModel) {
    val navController = rememberNavController()
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    var isDarkMode    by remember { mutableStateOf(false) }
    val theme         = if (isDarkMode) DarkKavyaTheme else LightKavyaTheme
    val auth          = FirebaseAuth.getInstance()
    val userName      = auth.currentUser?.displayName ?: "Kavya Reader"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        Triple("home",      Icons.Default.Home,     "Home"),
        Triple("listen",    Icons.Default.Headset,  "Listen"),
        Triple("bhavartha", Icons.Default.MenuBook, "Bhavartha"),
        Triple("poets",     Icons.Default.People,   "Poets"),
        Triple("search",    Icons.Default.Search,   "Search"),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                userName = userName,
                theme = theme,
                isDarkMode = isDarkMode,
                onDarkModeChange = { isDarkMode = it },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("ಕಾವ್ಯ ಕಣಜ", color = theme.textPrimary)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                "Menu",
                                tint = theme.accent
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = theme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = theme.surface) {
                    tabs.forEach { (route, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    icon,
                                    label,
                                    tint = if (currentRoute == route)
                                        theme.accent
                                    else
                                        theme.textSecondary
                                )
                            },
                            label = {
                                Text(
                                    label,
                                    color = if (currentRoute == route)
                                        theme.accent
                                    else
                                        theme.textSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = theme.accent.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavGraph(
                    navController = navController,
                    vm = poemViewModel,
                    theme = theme
                )
            }
        }
    }
}