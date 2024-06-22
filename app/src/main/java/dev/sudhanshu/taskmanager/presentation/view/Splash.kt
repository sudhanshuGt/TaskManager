package dev.sudhanshu.taskmanager.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import dev.sudhanshu.taskmanager.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.presentation.view.component.SettingsScreenWrapper
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.ui.theme.ThemeManager
import dev.sudhanshu.taskmanager.util.SettingsPreferences

@AndroidEntryPoint
class Splash : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    private lateinit var sharedPreferences : SettingsPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = SettingsPreferences(this)

        setContent {
            ThemeManager.isDarkModeEnabled = sharedPreferences.isDarkModeEnabled()
            SettingsScreenWrapper()
            TaskManagerTheme {
                SplashScreen(splashViewModel = splashViewModel) { isSignedIn ->
                    if (isSignedIn) {
                        startActivity(Intent(this, Home::class.java))
                    } else {
                        startActivity(Intent(this, Login::class.java))
                    }
                    finish()
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel,
    onNavigation: (Boolean) -> Unit
) {
    val uiState by splashViewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1000)
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is SplashViewModel.UiState.SignedIn -> onNavigation(true)
            is SplashViewModel.UiState.SignedOut -> onNavigation(false)
            else -> { /* No-op */ }
        }
    }

    TaskManagerTheme {
        Splash(alpha = alphaAnim.value)
    }
}

@Composable
fun Splash(alpha: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.alpha(alpha)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    Splash(alpha = 1f)
}
