package com.phone_box_app.ui.navhosts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.phone_box_app.ui.routes.AppRoutes
import com.phone_box_app.ui.routes.NavArgs


/**
 * Created by Ram Mandal on 23/01/2024
 * @System: Apple M1 Pro
 */

@Composable
fun SetUpNavActivityNavGraph(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.DetailScreen.route,
        modifier = modifier
    ) {
        composable(
            route = AppRoutes.DetailScreen.route,
            arguments = listOf(navArgument(NavArgs.MENU_TYPE) { type = NavType.StringType })
        ) { backStackEntry ->
            val menuTypeArg = backStackEntry.arguments?.getString(NavArgs.MENU_TYPE) ?: ""
        }

    }

    // Navigate immediately to the dynamic route once
    LaunchedEffect(startDestination) {
        navController.navigate(startDestination) {
            // Remove placeholder route from back stack so back goes to HomeActivity
            popUpTo(AppRoutes.DetailScreen.route) { inclusive = false }
            launchSingleTop = true
        }
    }
}

