package com.example.levelupmobile.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.levelupmobile.ui.screens.*

/**
 * onAddToCartFn te deja inyectar qué hacer al agregar al carrito.
 * Por ahora navega sin lógica de datos; más adelante conecta el VM/Repo.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    onAddToCartFn: (String) -> Unit = {}   // <-- inyectable (VM/Repo luego)
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        // HOME
        composable(Routes.Home.route) {
            HomeScreen(
                onOpenProduct = { pid ->
                    navController.navigate(Routes.ProductDetail.create(pid))
                },
                onAddToCart = { pid ->
                    onAddToCartFn(pid)   // por ahora no hace nada; luego VM.add(pid)
                },
                onGoCart = { navController.navigate(Routes.Cart.route) },
                onGoCheckout = { navController.navigate(Routes.Checkout.route) }
            )
        }

        // PRODUCT DETAIL (pid por ruta; la UI aún no lo usa directamente)
        composable(
            route = Routes.ProductDetail.route,
            arguments = listOf(navArgument(Routes.ProductDetail.ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            // Repo temporal -> luego se cambia a Room
            val repo = remember { com.example.levelupmobile.domain.repo.FakeShopRepository() }

            val vm: com.example.levelupmobile.vm.ProductDetailViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = com.example.levelupmobile.vm.factory.ProductDetailVMFactory(
                        repo = repo,
                        savedStateHandle = backStackEntry.savedStateHandle
                    )
                )

            val ui = vm.ui.collectAsState().value

            ProductDetailScreen(
                ui = ui,
                onAddToCart = { vm.addToCart() },
                onBack = { navController.popBackStack() }
            )
        }

        // CART
        composable(Routes.Cart.route) {
            CartScreen(
                onGoCheckout = { navController.navigate(Routes.Checkout.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // CHECKOUT
        composable(Routes.Checkout.route) {
            CheckoutScreen(
                onFinish = {
                    // después de finalizar, volvemos al Home
                    navController.popBackStack(Routes.Home.route, inclusive = false)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
