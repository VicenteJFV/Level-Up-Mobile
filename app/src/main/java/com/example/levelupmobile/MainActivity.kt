package com.example.levelupmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.levelupmobile.domain.repo.FakeShopRepository
import com.example.levelupmobile.nav.AppNavHost
import com.example.levelupmobile.ui.theme.LevelUpMobileTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LevelUpMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val nav = rememberNavController()

                    // ✅ Una sola instancia compartida del repo
                    val repo = androidx.compose.runtime.remember { FakeShopRepository() }

                    AppNavHost(
                        navController = nav,
                        repo = repo,
                        onAddToCartFn = { pid ->
                            // ✅ Agrega realmente al carrito del repo compartido
                            this@MainActivity.lifecycleScope.launch {
                                repo.addToCart(pid, 1)
                            }
                        }
                    )
                }
            }
        }
    }
}
