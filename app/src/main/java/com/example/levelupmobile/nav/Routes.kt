package com.example.levelupmobile.nav

import android.net.Uri

sealed class Routes(val route: String) {
    object Home : Routes("home")

    object ProductDetail : Routes("product/{pid}") {
        const val ARG = "pid"
        fun create(pid: String) = "product/${Uri.encode(pid)}"
    }

    object Cart : Routes("cart")
    object Checkout : Routes("checkout")

    // NUEVA RUTA: Pantalla de confirmación de compra
    object OrderSuccess : Routes("order_success/{orderId}") {
        const val ARG = "orderId"
        fun create(orderId: Long) = "order_success/$orderId"
    }
}