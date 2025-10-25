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
import com.example.levelupmobile.nav.AppNavHost
import com.example.levelupmobile.domain.repo.FakeShopRepository
import com.example.levelupmobile.domain.repo.ShopRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LevelUpMobileTheme {
                val nav = rememberNavController()
                val repo: ShopRepository = FakeShopRepository() // luego Room
                AppNavHost(navController = nav, repo = repo)
            }
        }
    }
}

