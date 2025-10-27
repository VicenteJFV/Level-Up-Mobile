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
                        style = MaterialTheme.typography.titleLarge,
                        color = ElectricBlue
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
                Text("Tu carrito está vacío", color = LightGrayText)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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

                Spacer(Modifier.height(12.dp))
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
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
            border = ButtonDefaults.outlinedButtonBorder
        ) { Text("Volver") }

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
        Text(label, color = LightGrayText)
        Text(
            value,
            color = NeonGreen,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        // Fila superior: imagen + (nombre, precio)
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Carga del drawable si existe
            val context = LocalContext.current
            val imageRes = item.imageUrl
                ?.let { context.resources.getIdentifier(it, "drawable", context.packageName) }
                ?.takeIf { it != 0 }
                ?: android.R.drawable.ic_menu_gallery

            Image(
                painter = painterResource(imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = NeonGreen,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.price.toCLP(),
                    color = ElectricBlue
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Fila inferior: controles de cantidad a la izquierda, "Eliminar" a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = { onDec(item.productId) },
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) { Text("−") }

                Text("${item.qty}", color = LightGrayText)

                OutlinedButton(
                    onClick = { onInc(item.productId) },
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) { Text("+") }
            }

            TextButton(onClick = { onRemove(item.productId) }) {
                Text("Eliminar", color = LightGrayText)
            }
        }
    }
}
