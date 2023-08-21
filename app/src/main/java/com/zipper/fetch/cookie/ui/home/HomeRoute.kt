package com.zipper.fetch.cookie.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.zipper.fetch.cookie.R
import com.zipper.fetch.cookie.data.UiDataProvider
import com.zipper.fetch.cookie.model.AppFunctionItems
import com.zipper.fetch.cookie.ui.AppScreen

@Composable
fun HomeRoute(
    onRoute: (AppScreen) -> Unit
){
    HomeScreen(onRoute = onRoute)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onRoute: (AppScreen) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "功能列表") }, actions = {
                Box(modifier = Modifier.padding(end = 16.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "None"
                    )
                }

            })
        },
        content = { paddingValues ->
            HomeScreenContent(
                modifier = Modifier.padding(paddingValues)
            ){
                onRoute(it.appScreen)
            }
        }
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier,
    onItemClicked: (AppFunctionItems) -> Unit
) {
    val list = remember {
        UiDataProvider.mainButtons
    }
    LazyColumn(modifier = modifier)  {
        items(
            items = list,
            itemContent = {
                HomeScreenListView(it, onItemClicked)
            }
        )
    }
}

@Composable
fun HomeScreenListView(appFunctionItems: AppFunctionItems, onItemClicked: (AppFunctionItems) -> Unit){
    Button(
        onClick = { onItemClicked(appFunctionItems) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("button-${appFunctionItems.name}")
    ) {
        Text(
            text = appFunctionItems.name,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}