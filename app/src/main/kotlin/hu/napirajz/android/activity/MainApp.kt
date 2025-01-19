package hu.napirajz.android.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hu.napirajz.android.provider.NavHostControllerProviders
import hu.napirajz.android.screen.ImageDetailsScreen
import hu.napirajz.android.screen.MainScreen
import hu.napirajz.android.theme.NapirajzTheme

val LocalNavHostController = compositionLocalOf<NavHostController> { error("NavHostController Not Found!") }

@Composable
fun MainApp() {
    NapirajzTheme {
        NavHostControllerProviders { navController ->
            NavHost(navController = navController, startDestination = "main") {
                composable("main") { MainScreen() }
                composable("details/{id}") {
                    val id = it.arguments?.getString("id")
                    ImageDetailsScreen(id)
                }
            }
        }
    }
}