package com.example.levelupmobile.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.ui.screens.*
import com.example.levelupmobile.vm.ProductDetailViewModel
import com.example.levelupmobile.vm.factory.ProductDetailVMFactory
import com.example.levelupmobile.vm.models.ProductUi

@Composable
fun AppNavHost(
    navController: NavHostController,
    repo: ShopRepository // pásalo desde MainActivity (Fake o Room)
) {
    NavHost(navController, startDestination = Routes.Home.route) {

        // Home
        composable(Routes.Home.route) {
            HomeScreen(
                onGoCart = { navController.navigate(Routes.Cart.route) },
                onGoCheckout = { navController.navigate(Routes.Checkout.route) },
                onGoProductDetail = { pid -> navController.navigate(Routes.ProductDetail.create(pid)) }
            )
        }

        // Product Detail (con arg)
        composable(Routes.ProductDetail.route) { backStackEntry ->
            // ViewModel con SavedStateHandle
            val factory = ProductDetailVMFactory(repo, backStackEntry.savedStateHandle)
            val vm: ProductDetailViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)

            val ui = vm.ui.collectAsState()

            ProductDetailScreen(
                ui = ui.value,
                onAddToCart = { vm.addToCart() },
                onBack = { navController.popBackStack() }
            )
        }

        // Cart
        composable(Routes.Cart.route) {
            CartScreen(
                onGoCheckout = { navController.navigate(Routes.Checkout.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // Checkout
        composable(Routes.Checkout.route) {
            CheckoutScreen(
                onFinish = { navController.popBackStack(Routes.Home.route, inclusive = false) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
