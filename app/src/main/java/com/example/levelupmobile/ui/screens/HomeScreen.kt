package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onGoCart: () -> Unit, onGoCheckout: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("LevelUp Shop") }) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Catálogo (placeholder)")
                Spacer(Modifier.height(16.dp))
                Button(onClick = onGoCart) { Text("Ir al Carrito") }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onGoCheckout) { Text("Ir a Checkout") }
            }
        }
    }
}
