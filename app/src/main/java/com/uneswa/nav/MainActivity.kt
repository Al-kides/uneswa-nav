package com.uneswa.nav

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uneswa.nav.data.LocationRepo
import com.uneswa.nav.ui.*

class MainActivity : ComponentActivity() {
    private val repo = LocationRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("uneswa_nav_prefs", Context.MODE_PRIVATE)
        
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            val initialDark = remember {
                if (prefs.contains("is_dark_mode")) {
                    prefs.getBoolean("is_dark_mode", false)
                } else null
            }
            var isDark by remember { mutableStateOf<Boolean?>(initialDark) }
            val currentDark = isDark ?: systemDark

            LaunchedEffect(currentDark) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = false
                }
            }
            
            AppTheme(useDarkTheme = currentDark) {
                Nav(repo, isDark) { dark ->
                    isDark = dark
                    prefs.edit().putBoolean("is_dark_mode", dark).apply()
                }
            }
        }
    }
}

@Composable
private fun Nav(repo: LocationRepo, isDark: Boolean?, onToggleDark: (Boolean) -> Unit) {
    val nav = rememberNavController()

    NavHost(nav, startDestination = "services") {

        composable("services") {
            StudentServicesScreen(
                onNavigate = { nav.navigate("home") },
                onLaptops = { nav.navigate("laptops") },
                onWifi = { nav.navigate("wifi") },
                isDark = isDark,
                onToggleDark = onToggleDark
            )
        }

        composable("wifi") {
            WifiInstructionsScreen(onBack = { nav.popBackStack() })
        }
        composable("laptops") {
            LaptopRecommenderScreen(onBack = { nav.popBackStack() })
        }

        composable("home") {
            val vm: HomeVM = viewModel(factory = VMFactory(repo))
            HomeScreen(
                vm = vm,
                onPick = { nav.navigate("directions/$it") },
                onServices = { nav.popBackStack() }
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
