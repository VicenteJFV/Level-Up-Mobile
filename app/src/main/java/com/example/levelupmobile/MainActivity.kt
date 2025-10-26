package com.example.levelupmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.levelupmobile.ui.theme.LevelUpMobileTheme
import com.example.levelupmobile.nav.AppNavHost
import com.example.levelupmobile.data.db.AppDatabase
import com.example.levelupmobile.data.local.ShopRepositoryImpl
import com.example.levelupmobile.domain.repo.ShopRepository
import com.example.levelupmobile.vm.CartViewModel
import com.example.levelupmobile.vm.factory.CartVMFactory

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
                    val context = LocalContext.current

                    // DB + Repo (Room) una sola vez
                    val db = remember { AppDatabase.get(context) }
                    val repo = remember { ShopRepositoryImpl(db.productDao(), db.cartDao()) }


                    // VM de carrito sencillo
                    val cartVm: CartViewModel = viewModel(factory = CartVMFactory(repo))


                    // Inyectamos todo a la navegación
                    AppNavHost(
                        navController = nav,
                        repo = repo,
                        onAddToCartFn = { pid ->
                            cartVm.addItem(pid)
                        }
                    )

                }
            }
        }
    }
}
