package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.CheckoutUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    ui: CheckoutUi,
    onName: (String) -> Unit,
    onPhone: (String) -> Unit,
    onAddress: (String) -> Unit,
    onDelivery: (String) -> Unit,
    onPayment: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💳 Checkout", color = ElectricBlue) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BlackBackground,
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onBack, enabled = !ui.isSubmitting) {
                    Text("Volver")
                }
                Button(
                    onClick = onSubmit,
                    enabled = ui.canSubmit && !ui.isSubmitting,
                    modifier = Modifier.weight(1f)
                ) {
                    if (ui.isSubmitting) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Finalizar compra")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nombre
            OutlinedTextField(
                value = ui.name, onValueChange = onName,
                label = { Text("Nombre completo") },
                isError = "name" in ui.errors,
                modifier = Modifier.fillMaxWidth()
            )
            Helper(ui.errors["name"])

            // Teléfono
            OutlinedTextField(
                value = ui.phone, onValueChange = onPhone,
                label = { Text("Teléfono (+56)") },
                isError = "phone" in ui.errors,
                modifier = Modifier.fillMaxWidth()
            )
            Helper(ui.errors["phone"])

            // Dirección
            OutlinedTextField(
                value = ui.address, onValueChange = onAddress,
                label = { Text("Dirección") },
                isError = "address" in ui.errors,
                modifier = Modifier.fillMaxWidth()
            )
            Helper(ui.errors["address"])

            // Método de entrega
            DeliveryDropdown(
                selected = ui.delivery,
                onSelect = onDelivery
            )

            // Método de pago
            PaymentDropdown(
                selected = ui.payment,
                onSelect = onPayment
            )
        }
    }
}

@Composable
private fun Helper(msg: String?) {
    if (msg != null) {
        Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}

// ----------------------
// 🔽 SELECT: ENTREGA (oscuro personalizado)
// ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDropdown(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Retiro en tienda", "Entrega a domicilio")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Método de entrega", color = LightGrayText) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGrayText,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGrayText,
                cursorColor = NeonGreen,
                focusedTextColor = NeonGreen,
                unfocusedTextColor = NeonGreen,
                focusedContainerColor = BlackCard,
                unfocusedContainerColor = BlackCard
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = BlackCard
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = NeonGreen) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ----------------------
// 🔽 SELECT: PAGO (oscuro personalizado)
// ----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDropdown(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Efectivo", "Débito", "Crédito")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Método de pago", color = LightGrayText) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGrayText,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGrayText,
                cursorColor = NeonGreen,
                focusedTextColor = NeonGreen,
                unfocusedTextColor = NeonGreen,
                focusedContainerColor = BlackCard,
                unfocusedContainerColor = BlackCard
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = BlackCard
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = NeonGreen) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
