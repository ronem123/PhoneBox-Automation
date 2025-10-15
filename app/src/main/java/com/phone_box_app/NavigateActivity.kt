package com.phone_box_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.phone_box_app.ui.components.CommonToolbar
import com.phone_box_app.ui.navhosts.SetUpNavActivityNavGraph
import com.phone_box_app.ui.routes.AppRoutes
import com.phone_box_app.ui.theme.ArchTheme
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Ram Mandal on 12/10/2025
 * @System: Apple M1 Pro
 */
@AndroidEntryPoint
class NavigateActivity : ComponentActivity() {

    companion object {
        private const val ARG_MENU_TYPE = "arg_menu_type"

        // Helper method to launch NavigateActivity with menu type
//        fun launch(context: Context, menuType: MenuType) {
//            val intent = Intent(context, NavigateActivity::class.java).apply {
//                putExtra(ARG_MENU_TYPE, menuType.name)
//            }
//            context.startActivity(intent)
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val menuTypeArg = intent.getStringExtra(ARG_MENU_TYPE).orEmpty()

        //re-construct to enum value
//        val menuType = MenuConfig.fromRouteArg(menuTypeArg)
//        val startArg =
//            MenuConfig.toRouteArg(menuType ?: MenuType.DOCUMENT_REQUIRED)//document_required

        val startRoute = /*when (menuType) {
            null -> AppRoutes.Home.route
            else -> AppRoutes.DetailScreen.withArgs(startArg)
        }*/
            AppRoutes.Home.route

        setContent {
            ArchTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        CommonToolbar {
                            if (!navController.navigateUp()) finish()
                        }
                    },
                ) { padding ->
                    // Setup NavGraph with startDestination based on menuType
                    SetUpNavActivityNavGraph(
                        modifier = Modifier.padding(padding),
                        navController = navController,
                        startDestination = startRoute
                    )
                }
            }
        }
    }
}