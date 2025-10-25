package com.example.levelupmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelupmobile.nav.Routes
import com.example.levelupmobile.ui.screens.*
import com.example.levelupmobile.ui.theme.LevelUpMobileTheme
import com.example.levelupmobile.nav.AppNavHost
import com.example.levelupmobile.domain.repo.FakeShopRepository
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.CartViewModel

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

                    val repo = remember { com.example.levelupmobile.domain.repo.FakeShopRepository() }
                    val cartVm = remember { com.example.levelupmobile.vm.CartViewModel(repo) }
                    AppNavHost(
                        navController = nav,
                        onAddToCartFn = { pid -> cartVm.addItem(pid)  }
                    )
                }
            }
        }

    }

}

