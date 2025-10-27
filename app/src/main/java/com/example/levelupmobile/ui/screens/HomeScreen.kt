package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.components.ProductCard
import com.example.levelupmobile.ui.theme.*

data class ProductItem(
    val id: String,
    val name: String,
    val priceLabel: String,
    val imageUrl: String? = null   // ← dejamos el nombre del drawable (p. ej. "ps5")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<ProductItem>,              // 👈 ahora recibe la lista
    onOpenProduct: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onGoCart: () -> Unit,
    onGoCheckout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🕹️ Level-Up Gamer",
                        style = MaterialTheme.typography.titleLarge,
                        color = ElectricBlue
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppButton(
                    text = "Ver Carrito",
                    onClick = onGoCart,
                    modifier = Modifier.weight(1f),
                    type = ButtonType.Primary
                )
                AppButton(
                    text = "Ir a Checkout",
                    onClick = onGoCheckout,
                    modifier = Modifier.weight(1f),
                    type = ButtonType.Secondary
                )
            }
        },
        containerColor = BlackBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { p ->                          // 👈 usa la lista recibida
                ProductCard(
                    name = p.name,
                    price = p.priceLabel,
                    imageUrl = p.imageUrl,              // nombre del drawable (ej: "ps5")
                    onClick = { onOpenProduct(p.id) },
                    onAddToCart = { onAddToCart(p.id) }
                )
            }
        }
    }
}
