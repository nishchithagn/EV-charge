package com.example.evgramacharge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.evgramacharge.screens.*
import com.example.evgramacharge.ui.theme.EvGramachargeTheme
import com.example.evgramacharge.viewmodel.AuthViewModel
import com.example.evgramacharge.viewmodel.EVViewModel // 👈 Imported your core live viewmodel package

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EvGramachargeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val evViewModel: EVViewModel = viewModel() // 👈 1. Created the single shared live data provider instance

    val startRoute = if (authViewModel.user == null) "login" else "role_selection"

    NavHost(navController = navController, startDestination = startRoute) {
        composable("login") {
            LoginScreen(
                onAuthSuccess = {
                    navController.navigate("role_selection") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable("role_selection") {
            RoleSelectionScreen(
                onNavigateToUser = { navController.navigate("user_home") },
                onNavigateToHost = { navController.navigate("host_dashboard") },
                onNavigateToProfile = { navController.navigate("profile") }
            )
        }

        composable("user_home") {
            UserHomeScreen(
                onNavigateToBooking = { pointId ->
                    navController.navigate("booking/$pointId")
                },
                evViewModel = evViewModel // 👈 2. Connected the live data stream to your home listings map
            )
        }

        composable("host_dashboard") {
            HostDashboardScreen(
                onNavigateToRegister = { navController.navigate("host_registration") }
            )
        }

        composable("host_registration") {
            HostRegistrationScreen(
                onRegistrationSuccess = { navController.popBackStack() },
                viewModel = evViewModel // 👈 3. Passed database engine reference to create host slots live
            )
        }

        composable("booking/{pointId}") { backStackEntry ->
            val pointId = backStackEntry.arguments?.getString("pointId") ?: ""
            BookingScreen(
                pointId = pointId,
                onBookingSuccess = { navController.navigate("success") },
                viewModel = evViewModel // 👈 4. Passed real-time engine reference to write reservations live
            )
        }

        composable("success") {
            SuccessScreen(
                onNavigateHome = {
                    navController.navigate("role_selection") {
                        popUpTo("role_selection") { inclusive = true }
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
