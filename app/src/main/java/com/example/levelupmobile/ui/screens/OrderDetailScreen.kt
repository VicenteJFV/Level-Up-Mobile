package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.OrderDetailUi
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    ui: OrderDetailUi,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onStartEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onConfirmOrder: () -> Unit,
    onCancelOrder: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pedido", color = ElectricBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = LightGrayText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BlackBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                ui.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ElectricBlue
                    )
                }
                ui.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌",
                            fontSize = 64.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = ui.error,
                            color = LightGrayText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        AppButton(
                            text = "Volver",
                            onClick = onBack,
                            type = ButtonType.Secondary
                        )
                    }
                }
                ui.order != null -> {
                    OrderContent(
                        ui = ui,
                        onStartEditing = onStartEditing,
                        onCancelEditing = onCancelEditing,
                        onPhoneChange = onPhoneChange,
                        onAddressChange = onAddressChange,
                        onSaveChanges = onSaveChanges,
                        onConfirmOrder = onConfirmOrder,
                        onCancelOrder = onCancelOrder
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderContent(
    ui: OrderDetailUi,
    onStartEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onConfirmOrder: () -> Unit,
    onCancelOrder: () -> Unit
) {
    val order = ui.order ?: return
    val formatter = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Estado del pedido
        item {
            StatusCard(
                status = order.status,
                hoursRemaining = ui.hoursRemaining,
                canEdit = ui.canEdit
            )
        }

        // Datos del cliente (editables)
        item {
            CustomerDataCard(
                ui = ui,
                onStartEditing = onStartEditing,
                onCancelEditing = onCancelEditing,
                onPhoneChange = onPhoneChange,
                onAddressChange = onAddressChange,
                onSaveChanges = onSaveChanges
            )
        }

        // Método de pago
        item {
            InfoCard(
                title = "Método de Pago",
                content = order.paymentMethod
            )
        }

        // Productos
        item {
            Text(
                "Productos del Pedido",
                color = ElectricBlue,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(order.items) { item ->
            ProductItemCard(
                name = item.productName,
                quantity = item.quantity,
                unitPrice = formatter.format(item.unitPrice),
                total = formatter.format(item.unitPrice * item.quantity)
            )
        }

        // Total
        item {
            TotalCard(total = formatter.format(order.totalAmount))
        }

        // Botones de acción
        item {
            ActionButtons(
                ui = ui,
                onConfirmOrder = onConfirmOrder,
                onCancelOrder = onCancelOrder
            )
        }
    }
}

@Composable
private fun StatusCard(
    status: String,
    hoursRemaining: Long,
    canEdit: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Estado",
                    color = LightGrayText,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    if (status == "CREATED") "📦 Pendiente" else "✅ Confirmado",
                    color = if (status == "CREATED") NeonGreen else ElectricBlue,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (canEdit && hoursRemaining > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "⏱️ Tiempo restante para editar: $hoursRemaining horas",
                    color = NeonGreen,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun CustomerDataCard(
    ui: OrderDetailUi,
    onStartEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSaveChanges: () -> Unit
) {
    val order = ui.order ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Datos del Cliente",
                    color = ElectricBlue,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (ui.canEdit && !ui.isEditing) {
                    IconButton(onClick = onStartEditing) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ElectricBlue)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Nombre (no editable)
            Text("Nombre:", color = LightGrayText, style = MaterialTheme.typography.bodySmall)
            Text(order.customerName, color = LightGrayText, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

            // Teléfono
            if (ui.isEditing) {
                OutlinedTextField(
                    value = ui.editPhone,
                    onValueChange = onPhoneChange,
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        focusedLabelColor = ElectricBlue,
                        cursorColor = ElectricBlue,
                        unfocusedTextColor = LightGrayText,
                        focusedTextColor = LightGrayText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text("Teléfono:", color = LightGrayText, style = MaterialTheme.typography.bodySmall)
                Text(order.customerPhone, color = LightGrayText, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(8.dp))

            // Dirección
            if (ui.isEditing) {
                OutlinedTextField(
                    value = ui.editAddress,
                    onValueChange = onAddressChange,
                    label = { Text("Dirección") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        focusedLabelColor = ElectricBlue,
                        cursorColor = ElectricBlue,
                        unfocusedTextColor = LightGrayText,
                        focusedTextColor = LightGrayText
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            } else {
                Text("Dirección:", color = LightGrayText, style = MaterialTheme.typography.bodySmall)
                Text(order.deliveryAddress, color = LightGrayText, style = MaterialTheme.typography.bodyLarge)
            }

            // Botones de edición
            if (ui.isEditing) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppButton(
                        text = "Cancelar",
                        onClick = onCancelEditing,
                        modifier = Modifier.weight(1f),
                        type = ButtonType.Secondary
                    )
                    AppButton(
                        text = "Guardar",
                        onClick = onSaveChanges,
                        modifier = Modifier.weight(1f),
                        type = ButtonType.Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = ElectricBlue, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Text(content, color = LightGrayText, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ProductItemCard(
    name: String,
    quantity: Int,
    unitPrice: String,
    total: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = LightGrayText, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Cantidad: $quantity × $unitPrice",
                    color = LightGrayText.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                total,
                color = NeonGreen,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun TotalCard(total: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Total",
                color = ElectricBlue,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                total,
                color = NeonGreen,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    ui: OrderDetailUi,
    onConfirmOrder: () -> Unit,
    onCancelOrder: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (ui.canConfirm) {
            AppButton(
                text = "✅ Confirmar Pedido",
                onClick = onConfirmOrder,
                modifier = Modifier.fillMaxWidth(),
                type = ButtonType.Primary
            )
        }

        if (ui.canCancel) {
            AppButton(
                text = "❌ Cancelar Pedido",
                onClick = onCancelOrder,
                modifier = Modifier.fillMaxWidth(),
                type = ButtonType.Secondary
            )
        }

        if (!ui.canEdit) {
            Text(
                text = if (ui.order?.status == "CONFIRMED")
                    "✅ Este pedido ya está confirmado"
                else
                    "⏱️ El tiempo para editar este pedido ha expirado",
                color = LightGrayText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}