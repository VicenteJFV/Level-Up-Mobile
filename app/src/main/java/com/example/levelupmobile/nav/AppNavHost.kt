package com.example.levelupmobile.nav

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.ui.screens.*
import com.example.levelupmobile.vm.CheckoutEvent
import com.example.levelupmobile.vm.CheckoutViewModel
import com.example.levelupmobile.vm.factory.CheckoutVMFactory
import com.example.levelupmobile.vm.models.toCLP
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController,
    repo: ShopRepository,
    onAddToCartFn: (String) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        // HOME
        composable(Routes.Home.route) {
            val products = repo.observeProducts().collectAsState(initial = emptyList()).value
            val cartLines = repo.observeCart().collectAsState(initial = emptyList()).value
            val cartCount = cartLines.sumOf { it.qty }

            val uiItems = products.map {
                ProductItem(
                    id = it.id,
                    name = it.name,
                    priceLabel = it.priceNeto.toCLP(),
                    imageUrl = it.imageUrl
                )
            }

            HomeScreen(
                items = uiItems,
                cartCount = cartCount,
                onOpenProduct = { pid -> navController.navigate(Routes.ProductDetail.create(pid)) },
                onAddToCart = { pid -> onAddToCartFn(pid) },
                onGoCart = { navController.navigate(Routes.Cart.route) },
                onGoCheckout = { navController.navigate(Routes.Checkout.route) }
            )
        }

        // PRODUCT DETAIL
        composable(
            route = Routes.ProductDetail.route,
            arguments = listOf(
                navArgument(Routes.ProductDetail.ARG) { type = NavType.StringType }
            )
        ) {
            val vm: com.example.levelupmobile.vm.ProductDetailViewModel =
                viewModel(factory = com.example.levelupmobile.vm.factory.ProductDetailVMFactory(repo))

            val ui = vm.ui.collectAsState().value

            ProductDetailScreen(
                ui = ui,
                onAddToCart = { vm.addToCart() },
                onBack = { navController.popBackStack() }
            )
        }

        // CART
        composable(Routes.Cart.route) {
            val vm: com.example.levelupmobile.vm.CartViewModel =
                viewModel(
                    factory = com.example.levelupmobile.vm.factory.CartVMFactory(repo)
                )

            val ui = vm.ui.collectAsState().value

            CartScreen(
                ui = ui,
                onInc = { vm.inc(it) },
                onDec = { vm.dec(it) },
                onRemove = { vm.removeItem(it) },
                onGoCheckout = { navController.navigate(Routes.Checkout.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // CHECKOUT
        composable(Routes.Checkout.route) {
            val vm: CheckoutViewModel = viewModel(factory = CheckoutVMFactory(repo))
            val ui by vm.ui.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                vm.events.collect { ev ->
                    when (ev) {
                        is CheckoutEvent.Success -> {
                            // Navega a la pantalla de éxito con el orderId
                            navController.navigate(Routes.OrderSuccess.create(ev.orderId)) {
                                // Limpia el checkout del back stack
                                popUpTo(Routes.Checkout.route) { inclusive = true }
                            }
                        }
                        is CheckoutEvent.Error -> {
                            snackbarHostState.showSnackbar("⚠️ ${ev.message}")
                        }
                    }
                }
            }

            CheckoutScreen(
                ui = ui,
                onName = vm::onName,
                onPhone = vm::onPhone,
                onAddress = vm::onAddress,
                onDelivery = vm::onDelivery,
                onPayment = vm::onPayment,
                onSubmit = vm::submit,
                onBack = { navController.popBackStack() },
                snackbarHostState = snackbarHostState,
                onSetLocation = vm::setLocation
            )
        }

        // ORDER SUCCESS (NUEVA PANTALLA)
        composable(
            route = Routes.OrderSuccess.route,
            arguments = listOf(
                navArgument(Routes.OrderSuccess.ARG) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong(Routes.OrderSuccess.ARG) ?: 0L
            val scope = rememberCoroutineScope()

            OrderSuccessScreen(
                orderId = orderId,
                onGoHome = {
                    // Limpia el carrito
                    scope.launch {
                        repo.clearCart()
                    }
                    // Navega a Home y limpia TODO el back stack
                    navController.navigate(Routes.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}