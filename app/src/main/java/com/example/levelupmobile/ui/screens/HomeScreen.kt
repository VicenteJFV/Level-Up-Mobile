// kotlin
// File: `app/src/main/java/com/example/levelupmobile/ui/screens/HomeScreen.kt`
package com.example.levelupmobile.ui.screens

import android.util.Log
import androidx. compose.animation.animateContentSize
import androidx.compose.animation. core.animateIntAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime. saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy. items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui. Modifier
import androidx.compose. ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text. style.TextAlign
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit. sp
import com.example.levelupmobile.R
import com.example.levelupmobile. ui.components.AppButton
import com.example.levelupmobile.ui.components.AppIconButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.components. ProductCard
import com.example. levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.ProductViewModel
import com.example.levelupmobile.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

// Nueva ruta que conecta ViewModel -> UI
@Composable
fun HomeRoute(
    vm:  ProductViewModel,
    cartCount:  Int = 0,
    onOpenProduct: (String) -> Unit = {},
    onAddToCart: (String) -> Unit = {},
    onGoCart: () -> Unit = {},
    onGoCheckout:  () -> Unit = {},
    onSearchOrder: (Long) -> Unit = {}
) {
    val products by vm.products.collectAsState()
    LaunchedEffect(products) {
        Log.d("HomeRoute", "Productos recibidos: ${products.size}")
        if (products.isNotEmpty()) Log.d("HomeRoute", "Primer producto: ${products. first()}")
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
    val priceLabel = nf.format(p. priceNeto)
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
    val imageUrl: String?  = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    items: List<ProductItem>,
    cartCount: Int,
    onOpenProduct: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onGoCart: () -> Unit,
    onGoCheckout:  () -> Unit,
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
                        style = MaterialTheme.typography. titleLarge,
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
                    .padding(AppDimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions. spacingMedium)
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
                    modifier = Modifier. weight(1f),
                    type = ButtonType.Secondary,
                    enabled = checkoutEnabled
                )
            }
        },
        containerColor = BlackBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                . fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
        ) {
            item { StoreHeader() }

            item {
                AppButton(
                    text = "Buscar Mi Orden",
                    onClick = { showSearchDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    type = ButtonType.Secondary
                )
                Spacer(Modifier.height(AppDimensions.spacingMedium))
            }

            items(items) { p ->
                ProductCard(
                    name = p.name,
                    price = p. priceLabel,
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
                style = MaterialTheme.typography. titleMedium. copy(
                    color = ElectricBlue,
                    fontWeight = FontWeight.Bold  // ✅ Agregado
                )
            )
        },
        text = {
            Column {
                Text(
                    "Ingresa el ID de tu pedido:",
                    style = MaterialTheme.typography.bodyMedium. copy(
                        color = WhiteText,
                        fontWeight = FontWeight.Bold  // ✅ Agregado
                    )
                )
                Spacer(Modifier.height(AppDimensions. spacingMedium))
                OutlinedTextField(
                    value = orderIdText,
                    onValueChange = {
                        orderIdText = it
                        error = false
                    },
                    label = { Text("ID del pedido") },
                    placeholder = { Text("Ejemplo: 42") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = error,
                    supportingText = if (error) {
                        { Text("Ingresa un ID válido", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = LightGrayText,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = LightGrayText,
                        cursorColor = ElectricBlue,
                        focusedTextColor = WhiteText,  // ✅ Cambiado de LightGrayText
                        unfocusedTextColor = WhiteText  // ✅ Cambiado de LightGrayText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            AppButton(
                text = "Buscar",
                onClick = {
                    val orderId = orderIdText.toLongOrNull()
                    if (orderId != null && orderId > 0) {
                        onSearch(orderId)
                    } else {
                        error = true
                    }
                },
                type = ButtonType.Primary
            )
        },
        dismissButton = {
            AppButton(
                text = "Cancelar",
                onClick = onDismiss,
                type = ButtonType. Outlined
            )
        },
        containerColor = BlackBackground
    )
}

@Composable
private fun StoreHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppDimensions.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Level-Up Logo",
            modifier = Modifier. size(200.dp)
        )
        Spacer(Modifier.height(AppDimensions.spacingSmall))
        Text(
            "Level-Up Your Gaming",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = ElectricBlue
            ),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Los mejores precios para gamers",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            color = WhiteText
        )
    }
}