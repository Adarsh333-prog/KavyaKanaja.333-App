
package com.app.kavyakanaja

import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {

    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    // Animation
    var startAnimation by remember {
        mutableStateOf(false)
    }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    // Google Sign-In launcher
    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val task =
                GoogleSignIn.getSignedInAccountFromIntent(
                    result.data
                )

            try {

                val account =
                    task.getResult(ApiException::class.java)

                val credential =
                    GoogleAuthProvider.getCredential(
                        account.idToken,
                        null
                    )

                isLoading = true

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->

                        isLoading = false

                        if (authTask.isSuccessful) {

                            // SAVE LOGIN STATE
                            context
                                .getSharedPreferences(
                                    "kavya_prefs",
                                    Context.MODE_PRIVATE
                                )
                                .edit()
                                .putBoolean(
                                    "logged_in",
                                    true
                                )
                                .apply()

                            onLoginSuccess()

                        } else {

                            errorMessage =
                                authTask.exception?.message
                                    ?: "Google Sign In Failed"
                        }
                    }

            } catch (e: ApiException) {

                isLoading = false

                errorMessage =
                    "Google Error: ${e.message}"
            }
        }
    }

    // Google Config
    val gso = GoogleSignInOptions.Builder(
        GoogleSignInOptions.DEFAULT_SIGN_IN
    )
        .requestIdToken(
            context.getString(
                R.string.default_web_client_id
            )
        )
        .requestEmail()
        .build()

    val googleSignInClient =
        GoogleSignIn.getClient(context, gso)

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5EDE0))
            .alpha(alphaAnim.value),

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,

            modifier = Modifier.padding(32.dp)
        ) {

            Text(
                text = "ಕಾವ್ಯ ಕಣಜ",

                fontSize = 42.sp,

                fontWeight = FontWeight.Bold,

                fontFamily = FontFamily.Serif,

                color = Color(0xFF5A3A10)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Poetry Granary",

                fontSize = 14.sp,

                color = Color(0xFF907050),

                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Explore the beauty of\nKannada literature",

                fontSize = 16.sp,

                color = Color(0xFF5A3A10),

                textAlign = TextAlign.Center,

                lineHeight = 24.sp,

                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Loading
            if (isLoading) {

                CircularProgressIndicator(
                    color = Color(0xFF8B4513)
                )

            } else {

                // GOOGLE LOGIN BUTTON
                Button(
                    onClick = {

                        launcher.launch(
                            googleSignInClient.signInIntent
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.Center,

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = "G",

                            fontSize = 20.sp,

                            fontWeight = FontWeight.Bold,

                            color = Color(0xFF4285F4)
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Text(
                            text = "Continue with Google",

                            fontSize = 16.sp,

                            fontWeight = FontWeight.Medium,

                            color = Color(0xFF333333)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // GUEST LOGIN BUTTON
                OutlinedButton(
                    onClick = {

                        // SAVE LOGIN STATE
                        context
                            .getSharedPreferences(
                                "kavya_prefs",
                                Context.MODE_PRIVATE
                            )
                            .edit()
                            .putBoolean(
                                "logged_in",
                                true
                            )
                            .apply()

                        onLoginSuccess()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),

                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = "Continue as Guest",

                        fontSize = 16.sp,

                        color = Color(0xFF5A3A10)
                    )
                }
            }

            // ERROR
            if (errorMessage.isNotEmpty()) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = errorMessage,

                    color = Color.Red,

                    fontSize = 13.sp,

                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "ಕನ್ನಡ ಸಾಹಿತ್ಯದ ಸೊಗಡನ್ನು ಅನುಭವಿಸಿ",

                fontSize = 12.sp,

                color = Color(0xFFC8A050),

                fontFamily = FontFamily.Serif,

                textAlign = TextAlign.Center
            )
        }
    }
}

