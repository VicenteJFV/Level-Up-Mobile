package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.components.ProductCard
import com.example.levelupmobile.ui.theme.*

data class ProductItem(
    val id: String,
    val name: String,
    val priceLabel: String,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenProduct: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onGoCart: () -> Unit,
    onGoCheckout: () -> Unit
) {
    val mock = listOf(
        ProductItem("CO001", "PlayStation 5", "$549.990 CLP", null),
        ProductItem("JM001", "Catan", "$29.990 CLP", null),
        ProductItem("MS001", "Mouse Logitech G502", "$49.990 CLP", null)
    )

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
            items(mock) { p ->
                ProductCard(
                    name = p.name,
                    price = p.priceLabel,
                    imageUrl = p.imageUrl,
                    onClick = { onOpenProduct(p.id) },
                    onAddToCart = { onAddToCart(p.id) }
                )
            }
        }
    }
}
