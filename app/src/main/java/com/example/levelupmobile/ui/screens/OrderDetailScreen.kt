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
import com.example.levelupmobile.ui.components.AppIconButton
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
                title = { 
                    Text(
                        "Detalle del Pedido",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ElectricBlue
                        )
                    )
                },
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
                            .padding(AppDimensions.paddingXLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌",
                            fontSize = 64.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(AppDimensions.spacingLarge))
                        Text(
                            text = ui.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(AppDimensions.paddingXLarge))
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
            .padding(AppDimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingLarge)
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
                style = MaterialTheme.typography.titleMedium
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
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    if (status == "CREATED") "📦 Pendiente" else "✅ Confirmado",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (status == "CREATED") NeonGreen else ElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            if (canEdit && hoursRemaining > 0) {
                Spacer(Modifier.height(AppDimensions.spacingSmall))
                Text(
                    "⏱️ Tiempo restante para editar: $hoursRemaining horas",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonGreen
                    )
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
        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.paddingLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Datos del Cliente",
                    style = MaterialTheme.typography.titleMedium
                )
                if (ui.canEdit && !ui.isEditing) {
                    IconButton(onClick = onStartEditing) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = ElectricBlue)
                    }
                }
            }

            Spacer(Modifier.height(AppDimensions.spacingMedium))

            // Nombre (no editable)
            Text("Nombre:", style = MaterialTheme.typography.bodySmall)
            Text(order.customerName, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(AppDimensions.spacingSmall))

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
                Text("Teléfono:", style = MaterialTheme.typography.bodySmall)
                Text(order.customerPhone, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(AppDimensions.spacingSmall))

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
                Text("Dirección:", style = MaterialTheme.typography.bodySmall)
                Text(order.deliveryAddress, style = MaterialTheme.typography.bodyLarge)
            }

            // Botones de edición
            if (ui.isEditing) {
                Spacer(Modifier.height(AppDimensions.spacingLarge))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
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
        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
    ) {
        Column(modifier = Modifier.padding(AppDimensions.paddingLarge)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(AppDimensions.spacingXSmall))
            Text(content, style = MaterialTheme.typography.bodyLarge)
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
        shape = RoundedCornerShape(AppDimensions.cornerRadiusSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Cantidad: $quantity × $unitPrice",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LightGrayText.copy(alpha = 0.7f)
                    )
                )
            }
            Text(
                total,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun TotalCard(total: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Total",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = ElectricBlue,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                total,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
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
        verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
    ) {
        if (ui.canConfirm) {
            AppIconButton(
                text = "Confirmar Pedido",
                icon = "✅",
                onClick = onConfirmOrder,
                modifier = Modifier.fillMaxWidth(),
                type = ButtonType.Primary
            )
        }

        if (ui.canCancel) {
            AppIconButton(
                text = "Cancelar Pedido",
                icon = "❌",
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
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}