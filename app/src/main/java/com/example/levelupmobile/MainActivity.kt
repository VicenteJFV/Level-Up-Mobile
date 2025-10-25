package com.example.levelupmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelupmobile.nav.Routes
import com.example.levelupmobile.ui.screens.*
import com.example.levelupmobile.ui.theme.LevelUpMobileTheme
import com.example.levelupmobile.nav.AppNavHost
import com.example.levelupmobile.domain.repo.FakeShopRepository
import com.example.levelupmobile.domain.repo.ShopRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.example.levelupmobile.ui.theme.LevelUpMobileTheme {
                androidx.compose.material3.Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val nav = androidx.navigation.compose.rememberNavController()
                    AppNavHost(
                        navController = nav,
                        onAddToCartFn = { /* conectar VM/Repo luego */ }
                    )
                }
            }
        }

    }

}

