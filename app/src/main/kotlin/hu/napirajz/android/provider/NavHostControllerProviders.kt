package hu.napirajz.android.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import hu.napirajz.android.activity.LocalNavHostController

@Composable
fun NavHostControllerProviders(
    navController: NavHostController = rememberNavController(),
    content: @Composable (navHostController: NavHostController) -> Unit
) {
    CompositionLocalProvider(value = LocalNavHostController provides navController) {
        content(navController)
    }
}

@Preview
@Composable
fun NavHostControllerProvidersPreview() {

}