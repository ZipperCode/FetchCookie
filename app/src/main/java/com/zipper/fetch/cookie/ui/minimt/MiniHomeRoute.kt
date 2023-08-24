package com.zipper.fetch.cookie.ui.minimt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zipper.fetch.cookie.dao.MiniAccount
import com.zipper.fetch.cookie.data.UiDataProvider
import com.zipper.fetch.cookie.ui.AppScreen
import com.zipper.fetch.core.ext.toDateFmt
import kotlinx.coroutines.launch

@Preview
@Composable
fun MiniHomeScreenPreview() {
//    val pageUiState = MiniPageUiState.Content()
//    val uiState = MiniUIState.NoData(true)
//    MiniHomeScreen(pageUiState = pageUiState, uiState = uiState, onRoute = {}) {
// //
//    }
}

@Composable
fun MiniHomeRoute(miniViewModel: MiniViewModel, onRoute: (AppScreen) -> Unit) {
    MiniHomeScreen(miniViewModel, onRoute = onRoute)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniHomeScreen(
    miniViewModel: MiniViewModel,
    onRoute: (AppScreen) -> Unit,
) {
    val pageUiState by miniViewModel.pageUiState.collectAsStateWithLifecycle()

    val loadingMessage by miniViewModel.loadingMessageUiState.collectAsStateWithLifecycle()

    val snackBarMessage by miniViewModel.snackBarMessageUiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "小程序茅台") },
                actions = {
                    IconButton(onClick = { onRoute(AppScreen.MiniLogin) }) {
                        Icon(Icons.Filled.AddCircle, null)
                    }
                },
            )
        },
        content = { paddingValues ->
            if (snackBarMessage != null) {
                LaunchedEffect(snackBarMessage) {
                    snackBarHostState.showSnackbar(snackBarMessage!!)
                }
            }

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                when (pageUiState) {
                    is MiniPageUiState.Loading -> {
                        LoadingContent(loadingMessage = loadingMessage ?: "加载中...")
                    }

                    is MiniPageUiState.Error -> {
                        ErrorContent(message = (pageUiState as MiniPageUiState.Error).message) {
                            miniViewModel.loadAccount()
                        }
                    }

                    is MiniPageUiState.Content -> {
                        val accounts by miniViewModel.accounts.collectAsStateWithLifecycle()
                        val clipManager = LocalClipboardManager.current
                        AccountListContent(
                            accounts,
                            onReLogin = {
                                onRoute(AppScreen.MiniLogin)
                            },
                            onAppointed = {
                                miniViewModel.appoint(it)
                            },
                            onCopyToken = {
                                clipManager.setText(AnnotatedString(it))
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar("复制成功: ${clipManager.getText()}")
                                }
                            },
                        )
                    }

                    else -> {}
                }
            }
        },
    )
}

@Composable
private fun MiniHomeContent(
    uiState: MiniAccountListUiState,
    onRefreshAccount: () -> Unit,
    hasDataContent: @Composable (MiniAccountListUiState.HasData) -> Unit,
) {
    LoadingContent(
        empty = when (uiState) {
            is MiniAccountListUiState.HasData -> false
            is MiniAccountListUiState.Empty -> uiState.isLoading
        },
        emptyContent = { FullScreenLoading() },
        loading = uiState.isLoading,
        onRefresh = onRefreshAccount,
    ) {
        when (uiState) {
            is MiniAccountListUiState.HasData -> hasDataContent(uiState)

            is MiniAccountListUiState.Empty -> {
                if (uiState.errorMessage?.isNotEmpty() == true) {
                    // if there are no posts, and no error, let the user refresh manually
                    TextButton(
                        onClick = onRefreshAccount,
                        Modifier.fillMaxSize(),
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountListContent(
    accountUiStateList: List<MiniAccountUiState>,
    modifier: Modifier = Modifier,
    onReLogin: (MiniAccountUiState) -> Unit,
    onAppointed: (MiniAccountUiState) -> Unit,
    onCopyToken: (String) -> Unit,
    state: LazyListState = rememberLazyListState(),
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "当前账号数量: ${accountUiStateList.size}")
//                Row {
//                    Button(onClick = { /*TODO*/ }) {
//                        Text(text = "一键校验账号状态")
//                    }
//                    Button(onClick = { /*TODO*/ }) {
//                        Text(text = "一键预约")
//                    }
//                }
            }
        }
        LazyColumn(state = state) {
            accountUiStateList.forEach { account ->
                item {
                    AccountItem(account, onReLogin, onAppointed, onCopyToken)
                }
            }
        }
    }
}

val AccountImageRound = RoundedCornerShape(16.dp)

@Preview
@Composable
fun AccountItemPreview() {
    val accountState = MiniAccountUiState(
        MiniAccount("13812345678", "token", 1, false, 0, 0),
        null,
    )
    AccountItem(
        miniAccountUiState = accountState,
        onReLogin = {
        },
        onAppointed = {
        },
        onCopyToken = {
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountItem(
    miniAccountUiState: MiniAccountUiState,
    onReLogin: (MiniAccountUiState) -> Unit,
    onAppointed: (MiniAccountUiState) -> Unit,
    onCopyToken: (String) -> Unit,
) {
    Card(
        onClick = { },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(AccountImageRound)
                        .background(UiDataProvider.colors.random()),
                ) {
                    Text(
                        text = miniAccountUiState.mini?.text ?: "不可用",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp),
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "手机号: ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                Text(text = miniAccountUiState.account.phone, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterVertically))
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                Text(text = "账号状态: ")
                if (miniAccountUiState.account.isExpired) {
                    Text(text = "已失效", color = Color.Red)
                } else {
                    Text(text = "已登录", color = Color.Green)
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "最后预约时间: ${miniAccountUiState.account.lastAppointTime.toDateFmt()}")
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "最后操作时间: ${miniAccountUiState.account.lastOperationTime.toDateFmt()}")
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                Button(onClick = { onReLogin(miniAccountUiState) }) {
                    Text(text = "重新登录")
                }
                Button(onClick = { onCopyToken(miniAccountUiState.account.token) }) {
                    Text(text = "复制Token")
                }
                Button(onClick = {
                    if (miniAccountUiState.isLoading) {
                        return@Button
                    }
                    onAppointed(miniAccountUiState)
                }) {
                    if (miniAccountUiState.isLoading) {
                        CircularProgressIndicator(color = Color.Yellow)
                    } else {
                        Text(text = "检查并预约")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
        )
    }
}

@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator()
    }
}
