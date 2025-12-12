package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.CartUiState
import com.example.levelupmobile.vm.models.CartItemUi
import com.example.levelupmobile.vm.models.toCLP
import androidx.compose.material3.Divider
import androidx.compose.foundation.layout.PaddingValues
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    ui: CartUiState,
    onInc: (String) -> Unit,
    onDec: (String) -> Unit,
    onRemove: (String) -> Unit,
    onGoCheckout: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🛒 Carrito de Compras",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ElectricBlue
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        bottomBar = {
            BottomSummaryBar(
                ui = ui,
                onBack = onBack,
                onGoCheckout = onGoCheckout
            )
        },
        containerColor = BlackBackground
    ) { padding ->
        if (ui.items.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tu carrito está vacío",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppDimensions.paddingMedium)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
                ) {
                    items(ui.items, key = { it.productId }) { item ->
                        CartRow(
                            item = item,
                            onInc = onInc,
                            onDec = onDec,
                            onRemove = onRemove
                        )
                        Divider(color = LightGrayText.copy(alpha = 0.15f))
                    }
                }

                Spacer(Modifier.height(AppDimensions.spacingMedium))
                SummaryRow(label = "Subtotal", value = ui.subtotal.toCLP())
                SummaryRow(label = "IVA (19%)", value = ui.iva.toCLP())
                SummaryRow(label = "Total", value = ui.total.toCLP(), bold = true)
            }
        }
    }
}

@Composable
private fun BottomSummaryBar(
    ui: CartUiState,
    onBack: () -> Unit,
    onGoCheckout: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(AppDimensions.paddingMedium),
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        AppButton(
            text = "Volver",
            onClick = onBack,
            type = ButtonType.Outlined
        )

        AppButton(
            text = "Proceder a Checkout",
            onClick = onGoCheckout,
            modifier = Modifier.weight(1f),
            type = ButtonType.Primary,
            enabled = ui.items.isNotEmpty()
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = NeonGreen,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun CartRow(
    item: CartItemUi,
    onInc: (String) -> Unit,
    onDec: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppDimensions.spacingMedium)
    ) {
        // Fila superior: imagen + (nombre, precio)
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Usar Coil para imágenes remotas (igual que ProductCard)
            val imageModifier = Modifier.size(72.dp)
            val imageUrl = item.imageUrl

            if (!imageUrl.isNullOrBlank()) {
                val lower = imageUrl.lowercase()
                if (lower.startsWith("http://") || lower.startsWith("https://")) {
                    // Imagen remota con Coil
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.name,
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                        error = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                } else {
                    // Imagen local (drawable)
                    val imageRes = context.resources.getIdentifier(imageUrl, "drawable", context.packageName)
                        .takeIf { it != 0 } ?: android.R.drawable.ic_menu_gallery

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = item.name,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // Placeholder si no hay imagen
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "placeholder",
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(AppDimensions.spacingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = NeonGreen,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(AppDimensions.spacingXSmall))
                Text(
                    text = item.price.toCLP(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ElectricBlue
                    )
                )
            }
        }

        Spacer(Modifier.height(AppDimensions.spacingSmall))

        // Fila inferior: controles de cantidad a la izquierda, "Eliminar" a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
            ) {
                OutlinedButton(
                    onClick = { onDec(item.productId) },
                    contentPadding = PaddingValues(horizontal = AppDimensions.paddingMedium)
                ) { Text("−") }

                Text("${item.qty}", style = MaterialTheme.typography.bodyMedium)

                OutlinedButton(
                    onClick = { onInc(item.productId) },
                    contentPadding = PaddingValues(horizontal = AppDimensions.paddingMedium)
                ) { Text("+") }
            }

            TextButton(onClick = { onRemove(item.productId) }) {
                Text("Eliminar", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
