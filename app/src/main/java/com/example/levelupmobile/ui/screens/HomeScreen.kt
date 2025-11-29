package com.example.levelupmobile.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelupmobile.R
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
    items: List<ProductItem>,
    cartCount: Int,
    onOpenProduct: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onGoCart: () -> Unit,
    onGoCheckout: () -> Unit,
    onSearchOrder: (Long) -> Unit
) {
    var showSearchDialog by remember { mutableStateOf(false) }

    val animatedCount by animateIntAsState(targetValue = cartCount, label = "cart-count")
    val cartText = if (animatedCount > 0) "Ver Carrito ($animatedCount)" else "Ver Carrito"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Level-Up Gamer",
                        style = MaterialTheme.typography.titleLarge,
                        color = ElectricBlue
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botón de búsqueda de orden
                AppButton(
                    text = "🔍 Buscar Mi Orden",
                    onClick = { showSearchDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 8.dp),
                    type = ButtonType.Secondary
                )

                // Botones originales
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppButton(
                        text = cartText,
                        onClick = onGoCart,
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize(),
                        type = ButtonType.Primary
                    )
                    AppButton(
                        text = "Ir a Checkout",
                        onClick = onGoCheckout,
                        modifier = Modifier.weight(1f),
                        type = ButtonType.Secondary
                    )
                }
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
            item { StoreHeader() }

            items(items) { p ->
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

    if (showSearchDialog) {
        SearchOrderDialog(
            onDismiss = { showSearchDialog = false },
            onSearch = { orderId ->
                showSearchDialog = false
                onSearchOrder(orderId)
            }
        )
    }
}

@Composable
private fun SearchOrderDialog(
    onDismiss: () -> Unit,
    onSearch: (Long) -> Unit
) {
    var orderIdText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Buscar Mi Orden",
                color = ElectricBlue
            )
        },
        text = {
            Column {
                Text(
                    "Ingresa el ID de tu pedido:",
                    color = LightGrayText,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = orderIdText,
                    onValueChange = {
                        orderIdText = it
                        error = false
                    },
                    label = { Text("ID del pedido") },
                    placeholder = { Text("Ejemplo: 1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = error,
                    supportingText = if (error) {
                        { Text("Ingresa un ID válido", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        focusedLabelColor = ElectricBlue,
                        cursorColor = ElectricBlue,
                        unfocusedTextColor = LightGrayText,
                        focusedTextColor = LightGrayText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val orderId = orderIdText.toLongOrNull()
                    if (orderId != null && orderId > 0) {
                        onSearch(orderId)
                    } else {
                        error = true
                    }
                }
            ) {
                Text("Buscar", color = ElectricBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = LightGrayText)
            }
        },
        containerColor = BlackBackground
    )
}

@Composable
private fun StoreHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo Level-Up Gamer",
            modifier = Modifier.size(110.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Los mejores precios y productos para darle un Level-Up a tu PC Gamer.",
            color = LightGrayText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(12.dp))
        Divider(color = LightGrayText.copy(alpha = 0.12f))
    }
}