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
//all todos are addresses. check previous diff.
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

    NavHost(nav, startDestination = "services") {

        composable("services") {
            StudentServicesScreen(onNavigate = { nav.navigate("home") })
        }

        composable("home") {
            val vm: HomeVM = viewModel(factory = VMFactory(repo))
            HomeScreen(
                vm = vm,
                onPick = { nav.navigate("directions/$it") },
                onServices = { nav.popBackStack() } // Back to services
            )
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
