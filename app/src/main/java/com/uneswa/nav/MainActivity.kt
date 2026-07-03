package com.uneswa.nav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uneswa.nav.data.LocationRepo
import com.uneswa.nav.ui.*


// ToDO : Add functionality to convert this into a student utility app more than just
//a navigation app to justify the apk's large size.


// Note: Not really sure how the UI will look but... actually, should not be an issue; Let me complete the basic functionality of
// the navigation side of the app.

// So, what I want and need is for app to give a gesture prompt, communicating to the user images slide left...
//and instructions go down. So I am thinking along the lines of... vignette, then prompt user???
// TODO: ^

class MainActivity : ComponentActivity() {
    private val repo = LocationRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { Nav(repo) } }
    }
}

@Composable
private fun Nav(repo: LocationRepo) {
    val nav = rememberNavController()

    NavHost(nav, startDestination = "home") {

        composable("home") {
            val vm: HomeVM = viewModel(factory = VMFactory(repo))
            HomeScreen(vm, onPick = { nav.navigate("directions/$it") })
        }

        composable(
            route     = "directions/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val id = it.arguments?.getString("id") ?: return@composable
            val vm: DirectionsVM = viewModel(factory = VMFactory(repo, id))
            DirectionsScreen(vm, onBack = { nav.popBackStack() })
        }
    }
}
