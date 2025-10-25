sealed class Routes(val route: String) {
    object Home : Routes("home")
    object ProductDetail : Routes("product/{productId}") {
        fun create(id: Int) = "product/$id"
    }
    object Cart : Routes("cart")
    object Checkout : Routes("checkout")
}
