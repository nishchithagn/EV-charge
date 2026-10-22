package com.example.evgramacharge.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object RoleSelection : Screen("role_selection")
    object HostRegistration : Screen("host_registration")
    object HostDashboard : Screen("host_dashboard")
    object UserHome : Screen("user_home")
    object Booking : Screen("booking/{stationId}") {
        fun createRoute(stationId: String) = "booking/$stationId"
    }
    object Calculator : Screen("calculator")
    object Profile : Screen("profile")
    object Success : Screen("success")
}
