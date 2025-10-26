package com.example.levelupmobile.nav

import android.net.Uri

sealed class Routes(val route: String) {
    object Home : Routes("home")

    object ProductDetail : Routes("product/{pid}") {
        const val ARG = "pid"
        fun create(pid: String) = "product/${Uri.encode(pid)}" // ← codifica el parámetro
    }

    object Cart : Routes("cart")
    object Checkout : Routes("checkout")
}
