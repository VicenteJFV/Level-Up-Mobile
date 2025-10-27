package com.example.levelupmobile.nav

import androidx.compose.foundation.layout.padding // ⬅️ IMPORTA LA EXTENSIÓN
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.ui.screens.*
import com.example.levelupmobile.vm.models.toCLP

// Imports para el Text temporal (puedes quitarlos cuando ya no lo uses)
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
            // DEBUG temporal para ver qué trae Room
            println("PRODUCTS -> " + products.joinToString { "${it.id}:${it.imageUrl}" })


            val uiItems = products.map {
                ProductItem(
                    id = it.id,
                    name = it.name,
                    priceLabel = it.priceNeto.toCLP(), // si prefieres formatear aquí
                    imageUrl = it.imageUrl             // ← viene de tu JSON en assets
                )
            }

            HomeScreen(
                items = uiItems,
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
            // ViewModel del carrito usando el MISMO repo compartido
            val vm: com.example.levelupmobile.vm.CartViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
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
