package com.zipper.fetch.cookie.ui.component

import android.annotation.SuppressLint
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun VerifyCodeButtonPrevious() {
    VerifyCodeButton( ) {
    }
}

@Composable
fun VerifyCodeButton(
    modifier: Modifier = Modifier,
    totalTime: Int = 60,
    buttonText: String = "发送验证码",
    onClick: () -> Unit,
) {
    var send by remember {
        mutableStateOf(false)
    }

    var timeStep by remember {
        mutableStateOf(totalTime)
    }

    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            if (send) {
                return@Button
            }
            onClick()
            send = true
            coroutineScope.launch {
                repeat(totalTime) {
                    delay(1000)
                    timeStep -= 1
                }
                send = false
                timeStep = totalTime
            }
        },
        modifier = modifier,
        enabled = !send,
    ) {
        if (send) {
            Text("${timeStep}s")
        } else {
            Text(buttonText)
        }
    }
}
