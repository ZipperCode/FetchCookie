package com.zipper.fetch.cookie.ui.minimt

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.zipper.fetch.cookie.data.UiDataProvider
import com.zipper.fetch.cookie.ui.component.VerifyCodeButton
import com.zipper.fetch.cookie.ui.minimt.model.IDrawDown
import com.zipper.fetch.cookie.ui.minimt.model.MiniProgramConfig
import com.zipper.fetch.lottie.LottieWorkingLoadingView

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        miniProgramItems = UiDataProvider.miniProgramItems,
        sendCodeAction = {
        },
        onLoginSuccess = {
        },
    )
}

@Composable
fun MiniLoginRoute(viewModel: MiniViewModel) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    miniProgramItems: List<MiniProgramConfig>,
    sendCodeAction: (String) -> Unit,
    onLoginSuccess: () -> Unit,
) {
    Scaffold { paddingValues ->

        // TextFields
        var phone by remember { mutableStateOf(TextFieldValue("")) }
        var code by remember { mutableStateOf(TextFieldValue("")) }
        var hasError by remember { mutableStateOf(false) }
        val phoneInteractionState = remember { MutableInteractionSource() }
        val codeInteractionState = remember { MutableInteractionSource() }
        var miniProgram by remember {
            mutableStateOf(miniProgramItems.firstOrNull())
        }

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { LottieWorkingLoadingView(context = LocalContext.current) }
            item {
                Text(
                    text = "茅台小程序登录",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 12.dp))
            }

            item {
                if (miniProgramItems.isNotEmpty()) {
                    DrawDownMiniProgram(miniProgramItems.first(), onItemSelected = {
                        miniProgram = it
                    }, items = miniProgramItems, modifier = Modifier.fillMaxWidth())
                } else {
                    OutlinedTextField(
                        value = "暂无可用的小程序类型",
                        onValueChange = {},
                        maxLines = 1,
                        enabled = false,
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = phone,
                    leadingIcon = {
                        Icon(Icons.Filled.Phone, null)
                    },
                    maxLines = 1,
                    isError = hasError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    label = { Text(text = "手机号") },
                    placeholder = { Text(text = "13812345678") },
                    onValueChange = {
                        phone = it
                    },
                    interactionSource = phoneInteractionState,
                )
            }
            item {
                OutlinedTextField(
                    value = code,
                    leadingIcon = {
                        Icon(Icons.Filled.MailOutline, null)
                    },
                    maxLines = 1,
                    isError = hasError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    label = { Text(text = "验证码") },
                    placeholder = { Text(text = "1234") },
                    onValueChange = {
                        code = it
                    },
                    trailingIcon = {
                        VerifyCodeButton(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .bounceClick(),
                            onClick = {
                                sendCodeAction(phone.text)
                            },
                        )
                    },
                    interactionSource = codeInteractionState,
                )
            }
            item {
                var loading by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        if (phone.text.isEmpty() || code.text.isEmpty()) {
                            hasError = true
                            loading = false
                        } else {
                            loading = true
                            hasError = false
                            onLoginSuccess.invoke()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                        .bounceClick(),
                ) {
                    if (loading) {
                        HorizontalDottedProgressBar()
                    } else {
                        Text(text = "登录")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : IDrawDown> DrawDownMiniProgram(
    defaultValue: T,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var valueState by remember {
        mutableStateOf(defaultValue)
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { textFieldSize = it.size.toSize() }
            .clickable { expanded = true },
    ) {
        OutlinedTextField(
            value = defaultValue.text,
            onValueChange = {
                onItemSelected(valueState)
            },
            enabled = false,
            singleLine = true,
            leadingIcon = leadingIcon,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    if (expanded) {
                        Icon(Icons.Filled.KeyboardArrowDown, "collapse")
                    } else {
                        Icon(Icons.Filled.KeyboardArrowRight, "expand")
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledBorderColor = MaterialTheme.colorScheme.inverseSurface,
                disabledLeadingIconColor = MaterialTheme.colorScheme.inverseSurface,
                disabledTextColor = MaterialTheme.colorScheme.inverseSurface,
                disabledTrailingIconColor = MaterialTheme.colorScheme.inverseSurface,
            ),
            modifier = modifier,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .requiredSizeIn(maxHeight = 300.dp)
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item.text)
                    },
                    onClick = {
                        valueState = items[index]
                        onItemSelected(items[index])
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun HorizontalDottedProgressBar() {
    val color = MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition(label = "")
    val state by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "",
    )

    DrawCanvas(state = state, color = color)
}

@Composable
fun DrawCanvas(
    state: Float,
    color: Color,
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        val radius = (4.dp).value
        val padding = (6.dp).value

        for (i in 1..5) {
            if (i - 1 == state.toInt()) {
                drawCircle(
                    radius = radius * 2,
                    brush = SolidColor(color),
                    center = Offset(
                        x = this.center.x + radius * 2 * (i - 3) + padding * (i - 3),
                        y = this.center.y,
                    ),
                )
            } else {
                drawCircle(
                    radius = radius,
                    brush = SolidColor(color),
                    center = Offset(
                        x = this.center.x + radius * 2 * (i - 3) + padding * (i - 3),
                        y = this.center.y,
                    ),
                )
            }
        }
    }
}

enum class ButtonState { Pressed, Idle }

fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.9f else 1f, label = "Btn Click")

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { },
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}
