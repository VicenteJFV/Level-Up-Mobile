// kotlin
// File: `app/src/main/java/com/example/levelupmobile/ui/screens/HomeScreen.kt`
package com.example.levelupmobile.ui.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelupmobile.R
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.AppIconButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.components.ProductCard
import com.example.levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.ProductViewModel
import com.example.levelupmobile.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

// Nueva ruta que conecta ViewModel -> UI
@Composable
fun HomeRoute(
    vm: ProductViewModel,
    cartCount: Int = 0,
    onOpenProduct: (String) -> Unit = {},
    onAddToCart: (String) -> Unit = {},
    onGoCart: () -> Unit = {},
    onGoCheckout: () -> Unit = {},
    onSearchOrder: (Long) -> Unit = {}
) {
    val products by vm.products.collectAsState()
    LaunchedEffect(products) {
        Log.d("HomeRoute", "Productos recibidos: ${products.size}")
        if (products.isNotEmpty()) Log.d("HomeRoute", "Primer producto: ${products.first()}")
    }

    val items = products.map { productToItem(it) }

    HomeScreen(
        items = items,
        cartCount = cartCount,
        onOpenProduct = onOpenProduct,
        onAddToCart = onAddToCart,
        onGoCart = onGoCart,
        onGoCheckout = onGoCheckout,
        onSearchOrder = onSearchOrder
    )
}

private fun productToItem(p: Product): ProductItem {
    // Mostrar precio en CLP (sin decimales). priceNeto se interpreta como pesos chilenos.
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
        maximumFractionDigits = 0
    }
    val priceLabel = nf.format(p.priceNeto)
    return ProductItem(
        id = p.id,
        name = p.name,
        priceLabel = priceLabel,
        imageUrl = p.imageUrl
    )
}

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

    // ✅ FIX 1: Solo mostrar el número si hay items
    val cartText = if (animatedCount > 0) "Ver Carrito ($animatedCount)" else "Ver Carrito"

    // ✅ FIX 2: Deshabilitar "Ir a Checkout" si el carrito está vacío
    val checkoutEnabled = animatedCount > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Level-Up Gamer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ElectricBlue
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppIconButton(
                    text = "Buscar Mi Orden",
                    icon = "🔍",
                    onClick = { showSearchDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimensions.paddingMedium)
                        .padding(top = AppDimensions.paddingSmall),
                    type = ButtonType.Secondary
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
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
                        type = ButtonType.Secondary,
                        enabled = checkoutEnabled  // ✅ Deshabilitar si carrito vacío
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
                .padding(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
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
                style = MaterialTheme.typography.titleMedium.copy(
                    color = ElectricBlue
                )
            )
        },
        text = {
            Column {
                Text(
                    "Ingresa el ID de tu pedido:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(AppDimensions.spacingMedium))
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
            .padding(top = AppDimensions.paddingSmall, bottom = AppDimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo Level-Up Gamer",
            modifier = Modifier.size(AppDimensions.imageLarge)
        )
        Spacer(Modifier.height(AppDimensions.spacingMedium))
        Text(
            text = "Los mejores precios y productos para darle un Level-Up a tu PC Gamer.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppDimensions.paddingXLarge)
        )
        Spacer(Modifier.height(AppDimensions.spacingMedium))
        Divider(color = LightGrayText.copy(alpha = 0.12f))
    }
}