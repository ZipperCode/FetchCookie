package com.zipper.fetch.cookie.ui.component

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
fun VerifyCodeButtonPrevious() {
    val inSendCode by remember {
        mutableStateOf(false)
    }

    VerifyCodeButton(inSendCode = inSendCode, onSend = {
    }, onTimeDone = {
    })
}

@Composable
fun VerifyCodeButton(
    modifier: Modifier = Modifier,
    inSendCode: Boolean = false,
    totalTime: Int = 60,
    buttonText: String = "发送验证码",
    onSend: () -> Unit,
    onTimeDone: () -> Unit,
) {
    var timeStep by remember {
        mutableStateOf(totalTime)
    }

    val coroutineScope = rememberCoroutineScope()

    var job by remember {
        mutableStateOf<Job?>(null)
    }

    Button(
        onClick = {
            if (inSendCode) {
                return@Button
            }
            onSend()
            job?.cancel()
            job = coroutineScope.launch {
                repeat(totalTime) {
                    delay(1000)
                    timeStep -= 1
                }
                onTimeDone()
                timeStep = totalTime
            }
        },
        modifier = modifier,
        enabled = !inSendCode,
    ) {
        if (inSendCode) {
            Text("${timeStep}s")
        } else {
            Text(buttonText)
        }
    }
}
