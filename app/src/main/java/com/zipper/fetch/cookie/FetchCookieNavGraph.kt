package com.zipper.fetch.cookie

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zipper.fetch.cookie.ui.AppDestination.MTScreenRoute
import com.zipper.fetch.cookie.ui.AppDestination.MainScreenRoute
import com.zipper.fetch.cookie.ui.AppDestination.MiniMTAccountScreenRoute
import com.zipper.fetch.cookie.ui.home.HomeRoute
import com.zipper.fetch.cookie.ui.minimt.MiniHomeRoute
import com.zipper.fetch.cookie.ui.minimt.MiniViewModel
import com.zipper.fetch.cookie.util.StoreManager

@Composable
fun FetchCookieNavGraph(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = MainScreenRoute,
) {
    Log.d("BAAA", "FetchCookieNavGraph")

    val navigationActions = remember(navHostController) {
        AppNavActions(navHostController)
    }

    val context = LocalContext.current
    val dataStore by remember(context) {
        mutableStateOf(StoreManager(context))
    }
    val miniViewModel: MiniViewModel = viewModel(
        factory = MiniViewModel.provideFactory(dataStore),
    )

    NavHost(navController = navHostController, startDestination = startDestination, modifier = modifier) {
        composable(MainScreenRoute) {
            Log.d("BAAA", "MainScreenRoute")
            HomeRoute(onRoute = navigationActions.navigateRoute)
        }

        composable(MTScreenRoute) {
            Log.d("BAAA", "MTScreenRoute")

            MiniHomeRoute(miniViewModel) {
            }
        }

        composable(
            route = "$MiniMTAccountScreenRoute/{type}",
            arguments = listOf(
                navArgument("type") {
                    type = NavType.IntType
                    defaultValue = 0
                    nullable = false
                },
            ),
        ) {
//            val type by remember {
//                mutableStateOf(it.arguments!!.getInt("type"))
//            }
//
//            val homeViewModel: MiniMTViewModel = viewModel(
//                factory = MiniMTViewModel.provideFactory(type)
//            )
//            Log.d("BAAA", "MTScreenRoute $type $homeViewModel")
//            // MiniHomeRoute(homeViewModel)
        }
    }
}

private class AppNavActions(val navController: NavHostController) {

    val navigateRoute: (String) -> Unit = {
        navController.navigate(it)
    }
}
