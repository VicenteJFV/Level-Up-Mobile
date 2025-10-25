package com.example.levelupmobile.nav

sealed class Routes(val route: String) {
    object Home : Routes("home")

    object ProductDetail : Routes("product/{pid}") {
        const val ARG = "pid"
        fun create(pid: String) = "product/$pid"
    }

    object Cart : Routes("cart")
    object Checkout : Routes("checkout")
}
