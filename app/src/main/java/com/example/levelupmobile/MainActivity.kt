package com.example.levelupmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelupmobile.nav.Routes
import com.example.levelupmobile.ui.screens.*
import com.example.levelupmobile.ui.theme.LevelUpMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = com.example.levelupmobile.domain.repo.FakeShopRepository()
        // TODO: luego cambiar por ShopRepositoryImpl(Room)
        setContent {
            LevelUpMobileTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val nav = rememberNavController()
                    NavHost(navController = nav, startDestination = Routes.HOME) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                onGoCart = { nav.navigate(Routes.CART) },
                                onGoCheckout = { nav.navigate(Routes.CHECKOUT) }
                            )
                        }
                        composable(Routes.CART) {
                            CartScreen(
                                onGoCheckout = { nav.navigate(Routes.CHECKOUT) },
                                onBack = { nav.popBackStack() }
                            )
                        }
                        composable(Routes.CHECKOUT) {
                            CheckoutScreen(
                                onFinish = { nav.popBackStack(Routes.HOME, false) },
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
