package com.zipper.fetch.cookie.ui.minimt

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zipper.fetch.cookie.R
import com.zipper.fetch.cookie.dao.MiniAccount
import com.zipper.fetch.cookie.ui.AppDestination
import com.zipper.fetch.cookie.ui.AppScreen

@Preview
@Composable
fun MiniHomeScreenPreview() {
    val pageUiState = MiniPageUiState.Content()
    val uiState = MiniUIState.NoData(true)
    MiniHomeScreen(pageUiState = pageUiState, uiState = uiState, onRoute = {}) {
//
    }
}

@Composable
fun MiniHomeRoute(miniViewModel: MiniViewModel, onRoute: (AppScreen) -> Unit) {
    val uiState by miniViewModel.uiState.collectAsStateWithLifecycle()
    val pageUiState by miniViewModel.pageUiState.collectAsStateWithLifecycle()
    MiniHomeScreen(pageUiState, uiState, onRoute = onRoute) {
        miniViewModel.loadAccountList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniHomeScreen(
    pageUiState: MiniPageUiState,
    uiState: MiniUIState,
    onRoute: (AppScreen) -> Unit,
    onRefreshAccount: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
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
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                when (pageUiState) {
                    is MiniPageUiState.Loading -> {
                        LoadingContent()
                    }

                    is MiniPageUiState.Error -> {
                        ErrorContent()
                    }

                    is MiniPageUiState.Content -> {
                        MiniHomeContent(uiState, onRefreshAccount = onRefreshAccount) { uiState: MiniUIState.HasAccount, modifier ->
                            AccountListContent(uiState.accountList, modifier = modifier, onItemClicked = {
                            }, onLoginClicked = {
                            })
                        }
                    }

                    else -> {}
                }
            }


        },
    )
}

@Composable
private fun MiniHomeContent(
    uiState: MiniUIState,
    modifier: Modifier = Modifier,
    onRefreshAccount: () -> Unit,
    hasDataContent: @Composable (MiniUIState.HasAccount, Modifier) -> Unit,
) {
    LoadingContent(
        empty = when (uiState) {
            is MiniUIState.HasAccount -> false
            is MiniUIState.NoData -> uiState.isLoading
        },
        emptyContent = { FullScreenLoading() },
        loading = uiState.isLoading,
        onRefresh = onRefreshAccount,
    ) {
        when (uiState) {
            is MiniUIState.HasAccount -> hasDataContent(uiState, modifier)

            is MiniUIState.NoData -> {
                if (uiState.errorMessage?.isNotEmpty() == true) {
                    // if there are no posts, and no error, let the user refresh manually
                    TextButton(
                        onClick = onRefreshAccount,
                        modifier.fillMaxSize(),
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
    accountList: List<MiniAccount>,
    modifier: Modifier = Modifier,
    onItemClicked: (MiniAccount) -> Unit,
    onLoginClicked: (MiniAccount) -> Unit,
    state: LazyListState = rememberLazyListState(),
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "当前账号数量: ${accountList.size}")
                Row {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "一键校验账号状态")
                    }
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "一键预约")
                    }
                }
            }
        }
        LazyColumn(state = state) {
            accountList.forEach { account ->
                item {
                    AccountItem(account, onItemClicked, onLoginClicked)
                }
            }
        }
    }
}

val AccountImageRound = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountItem(
    account: MiniAccount,
    onItemClicked: (MiniAccount) -> Unit,
    onLoginClicked: (MiniAccount) -> Unit
) {
    Card(
        onClick = { onItemClicked(account) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {

        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clip(AccountImageRound)
                        .background(Color.Red)
                ) {
                    Text(
                        text = "遵义出山",
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
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(text = account.phone, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterVertically))

            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "账号状态: 已登录")
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "最后预约时间: 2023-11-11 11:11:11")
            Spacer(modifier = Modifier.height(5.dp))
            Row {
                if (account.isExpired) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "重新登录")
                    }
                }

//                Button(onClick = { /*TODO*/ }) {
//                    Text(text = "复制Token")
//                }
//                Button(onClick = { /*TODO*/ }) {
//                    Text(text = "检查并预约")
//                }
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
