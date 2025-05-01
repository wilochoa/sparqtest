package com.sensate.sparqtest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sensate.sparqtest.presentation.itemdetail.ItemDetailScreen
import com.sensate.sparqtest.presentation.itemlist.ItemListScreen

/**
 * Object containing navigation routes for the app.
 */
object AppDestinations {
    const val ITEM_LIST_ROUTE = "item_list"
    const val ITEM_DETAIL_ROUTE = "item_detail"
    const val ITEM_ID_ARG = "itemId"
    
    /**
     * Creates a route to the item detail screen with the item ID.
     * @param itemId The ID of the item to show details for.
     * @return The complete route string.
     */
    fun itemDetailRoute(itemId: String): String {
        return "$ITEM_DETAIL_ROUTE/$itemId"
    }
}

/**
 * Main navigation component for the app.
 * @param navController The NavHostController to use for navigation.
 * @param startDestination The starting destination route.
 * @param modifier Modifier for the NavHost.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = AppDestinations.ITEM_LIST_ROUTE,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Item List Screen
        composable(AppDestinations.ITEM_LIST_ROUTE) {
            ItemListScreen(
                onItemClick = { itemId ->
                    navController.navigate(AppDestinations.itemDetailRoute(itemId))
                }
            )
        }
        
        // Item Detail Screen
        composable(
            route = "${AppDestinations.ITEM_DETAIL_ROUTE}/{${AppDestinations.ITEM_ID_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.ITEM_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) {
            ItemDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
} 