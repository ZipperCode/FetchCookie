package com.zipper.fetch.cookie.ui.minimt

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.zipper.fetch.cookie.R
import com.zipper.fetch.cookie.dao.MiniAccount

@Preview
@Composable
fun MiniHomeScreenPreview() {
    val pageUiState = MiniPageUiState.Content(emptyList())
    val uiState = MiniUIState.NoData(true)
    MiniHomeScreen(pageUiState = pageUiState, uiState = uiState, onRoute = {}) {
//
    }
}

@Composable
fun MiniHomeRoute(miniViewModel: MiniViewModel, onRoute: (String) -> Unit) {
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
    onRoute: (String) -> Unit,
    onRefreshAccount: () -> Unit,
) {
    when (pageUiState) {
        is MiniPageUiState.Loading -> {
            LoadingContent()
        }

        is MiniPageUiState.Error -> {
            ErrorContent()
        }

        is MiniPageUiState.Content -> {
            val snackBarHostState = remember { SnackbarHostState() }
            Scaffold(
                snackbarHost = { SnackbarHost(snackBarHostState) },
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(text = "小程序茅台") },
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Filled.Settings, null)
                            }
                        },
                    )
                },
                content = { paddingValues ->
                    MiniHomeContent(
                        uiState,
                        modifier = Modifier.padding(paddingValues),
                        onRefreshAccount,
                    ) { uiState: MiniUIState.HasAccount, modifier ->
                        AccountListContent(uiState.accountList, modifier = modifier, onItemClicked = {
                        }, onLoginClicked = {
                        })
                    }
                },
            )
        }

        else -> {}
    }
}

@Composable
private fun MiniHomeContent(
    uiState: MiniUIState,
    modifier: Modifier,
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
            Text(text = "当前账号数量: ${accountList.size}")
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
fun AccountItem(account: MiniAccount, onItemClicked: (MiniAccount) -> Unit, onLoginClicked: (MiniAccount) -> Unit) {
    Card(
        onClick = { onItemClicked(account) },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Row() {
            Image(
                painterResource(R.mipmap.mt_logo_zycs),
                null,
                modifier = Modifier
                    .padding(16.dp)
                    .size(100.dp, 100.dp)
                    .clip(AccountImageRound),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "手机: ${account.phone}")
                Button(onClick = { onLoginClicked(account) }) {
                    Text(text = "去登陆")
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
