package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onFinish: () -> Unit, onBack: () -> Unit = {}) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Checkout") }) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Formulario (placeholder)")
                Spacer(Modifier.height(12.dp))
                Button(onClick = onFinish) { Text("Finalizar pedido") }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onBack) { Text("Volver") }
            }
        }
    }
}
