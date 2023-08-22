package com.zipper.fetch.cookie

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.zipper.fetch.cookie.ui.AppScreen
import com.zipper.fetch.cookie.ui.theme.FetchCookieTheme
import com.zipper.fetch.cookie.ui.theme.SystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            FetchCookieApp(window)
        }
    }
}

@Composable
fun FetchCookieApp(window: Window) {
    val systemUiController = remember { SystemUiController(window) }
    FetchCookieTheme(systemUiController = systemUiController) {
        FetchCookieContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchCookieContent() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        color = MaterialTheme.colorScheme.background,
    ) {
        val navController = rememberNavController()
        FetchCookieNavGraph(navController, startDestination = AppScreen.MiniHome)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FetchCookieContent()
}
