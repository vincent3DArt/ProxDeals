package com.prox.deals

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prox.deals.ui.screens.DealsScreen
import com.prox.deals.ui.screens.DetailScreen
import com.prox.deals.ui.screens.SavedScreen

/**
 * Route names kept in one place so we don't mistype strings around the app.
 */
object Routes {
    const val DEALS = "deals"
    const val SAVED = "saved"
    const val DETAIL = "detail/{productId}" // {productId} is filled in at runtime
    fun detail(productId: Int) = "detail/$productId"
}

/**
 * Sets up navigation between the three screens. We create ONE DealsViewModel
 * here and pass it to every screen, so saved deals and filters are shared.
 */
@Composable
fun ProxNavHost() {
    val navController = rememberNavController()
    val vm: DealsViewModel = viewModel() // survives rotation, shared by all screens

    NavHost(navController = navController, startDestination = Routes.DEALS) {

        composable(Routes.DEALS) {
            DealsScreen(
                vm = vm,
                onDealClick = { id -> navController.navigate(Routes.detail(id)) },
                onSavedClick = { navController.navigate(Routes.SAVED) }
            )
        }

        composable(Routes.SAVED) {
            SavedScreen(
                vm = vm,
                onDealClick = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productId") ?: 0
            DetailScreen(
                productId = id,
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
