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
import com. example.levelupmobile.ui.screens.ProductDetailScreen
import com.example.levelupmobile.vm.CheckoutEvent
import com.example.levelupmobile.vm.CheckoutViewModel
import com.example.levelupmobile.vm.OrderEvent
import com.example.levelupmobile.vm.OrderViewModel
import com.example.levelupmobile.vm.factory.CheckoutVMFactory
import com.example.levelupmobile.vm.factory.OrderVMFactory
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
                onGoCheckout = { navController.navigate(Routes.Checkout.route) },
                onSearchOrder = { orderId -> navController.navigate(Routes.OrderDetail.create(orderId)) }
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
                            navController.navigate(Routes.OrderSuccess.create(ev.orderId)) {
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

        // ORDER SUCCESS
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
                    scope.launch {
                        repo.clearCart()
                    }
                    navController.navigate(Routes.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ORDER DETAIL (NUEVA PANTALLA)
        composable(
            route = Routes.OrderDetail.route,
            arguments = listOf(
                navArgument(Routes.OrderDetail.ARG) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong(Routes.OrderDetail.ARG) ?: 0L
            val vm: OrderViewModel = viewModel(factory = OrderVMFactory(repo))
            val ui by vm.ui.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(orderId) {
                vm.searchOrder(orderId)
            }

            LaunchedEffect(Unit) {
                vm.events.collect { event ->
                    when (event) {
                        is OrderEvent.OrderCancelled -> {
                            snackbarHostState.showSnackbar("✅ Pedido cancelado")
                            navController.popBackStack()
                        }
                        is OrderEvent.OrderConfirmed -> {
                            snackbarHostState.showSnackbar("✅ Pedido confirmado")
                        }
                        is OrderEvent.OrderUpdated -> {
                            snackbarHostState.showSnackbar("✅ Pedido actualizado")
                        }
                        is OrderEvent.Error -> {
                            snackbarHostState.showSnackbar("⚠️ ${event.message}")
                        }
                    }
                }
            }

            OrderDetailScreen(
                ui = ui,
                snackbarHostState = snackbarHostState,
                onBack = { navController.popBackStack() },
                onStartEditing = vm::startEditing,
                onCancelEditing = vm::cancelEditing,
                onPhoneChange = vm::onPhoneChange,
                onAddressChange = vm::onAddressChange,
                onSaveChanges = vm::saveChanges,
                onConfirmOrder = vm::confirmOrder,
                onCancelOrder = vm::cancelOrder
            )
        }
    }
}