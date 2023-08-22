package com.zipper.fetch.cookie

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zipper.fetch.cookie.dao.AppDataBase
import com.zipper.fetch.cookie.ui.AppScreen
import com.zipper.fetch.cookie.ui.home.HomeRoute
import com.zipper.fetch.cookie.ui.minimt.MiniHomeRoute
import com.zipper.fetch.cookie.ui.minimt.MiniLoginRoute
import com.zipper.fetch.cookie.ui.minimt.MiniRepository
import com.zipper.fetch.cookie.ui.minimt.MiniViewModel
import com.zipper.fetch.cookie.util.StoreManager

private fun NavGraphBuilder.composable(
    screen: AppScreen,
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(screen.route, content = content)
}

@Composable
fun FetchCookieNavGraph(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: AppScreen = AppScreen.MiniHome,
) {
    Log.d("BAAA", "FetchCookieNavGraph")

    val navigationActions = remember(navHostController) {
        AppNavActions(navHostController)
    }

    val miniViewModel: MiniViewModel = viewModel(
        factory = MiniViewModel.provideFactory(MiniRepository(AppDataBase.current.getMiniAccountDao())),
    )

    NavHost(navController = navHostController, startDestination = startDestination.route, modifier = modifier) {
        composable(AppScreen.App) {
            Log.d("BAAA", "MainScreenRoute")
            HomeRoute(onRoute = navigationActions.navigateRoute)
        }

        composable(AppScreen.MiniHome) {
            MiniHomeRoute(miniViewModel, onRoute = navigationActions.navigateRoute)
        }

        composable(AppScreen.MiniLogin) {
            MiniLoginRoute(miniViewModel, onPopBackStack = navigationActions.popBackStack)
        }
    }
}

private class AppNavActions(val navController: NavHostController) {

    val navigateRoute: (AppScreen) -> Unit = {
        navController.navigate(it.route)
    }

    val popBackStack: () -> Unit = {
        navController.popBackStack()
    }
}
