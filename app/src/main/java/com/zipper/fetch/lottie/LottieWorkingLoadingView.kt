package com.zipper.fetch.lottie

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zipper.fetch.lottie.LottieLoadingView

@Composable
fun LottieWorkingLoadingView(context: Context) {
    LottieLoadingView(
        context = context,
        file = "working.json",
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
