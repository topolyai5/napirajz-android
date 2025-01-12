package hu.napirajz.android.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import hu.napirajz.android.provider.NavHostControllerProviders
import hu.napirajz.android.screen.RandomRajzScreen
import hu.napirajz.android.theme.NapirajzTheme

val LocalNavHostController = compositionLocalOf<NavHostController> { error("NavHostController Not Found!") }

@Composable
fun MainApp() {
    NapirajzTheme {
        NavHostControllerProviders { navController ->
            NavHost(navController = navController, startDestination = "random") {
                composable("random") { RandomRajzScreen() }
            }
        }
    }
}