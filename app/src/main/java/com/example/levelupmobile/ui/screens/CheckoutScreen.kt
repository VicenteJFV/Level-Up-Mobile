package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.*
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import com.example.levelupmobile.common.StoreLocations
import com.example.levelupmobile.ui.components.LocationButton
import com.example.levelupmobile.ui.components.StoreMapButton
import com.example.levelupmobile.ui.components.reverseGeocode
import com.example.levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.CheckoutUi
import kotlinx.coroutines.launch

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
    snackbarHostState: SnackbarHostState,
    onSetLocation: (Double, Double) -> Unit,
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
                OutlinedButton(
                    onClick = onBack,
                    enabled = !ui.isSubmitting,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ElectricBlue,
                        disabledContentColor = LightGrayText.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, ElectricBlue.copy(alpha = if (ui.isSubmitting) 0.5f else 1f))
                ) {
                    Text("Volver")
                }

                // ✅ FIX 3: Botón con bordes visibles cuando está deshabilitado
                Button(
                    onClick = onSubmit,
                    enabled = ui.canSubmit && !ui.isSubmitting,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = BlackBackground,
                        disabledContainerColor = BlackCard, // ✅ Fondo oscuro cuando está deshabilitado
                        disabledContentColor = LightGrayText.copy(alpha = 0.6f) // ✅ Texto gris cuando está deshabilitado
                    ),
                    border = if (ui.canSubmit && !ui.isSubmitting) {
                        null // Sin borde cuando está habilitado
                    } else {
                        BorderStroke(1.dp, LightGrayText.copy(alpha = 0.5f)) // ✅ Borde visible cuando está deshabilitado
                    }
                ) {
                    if (ui.isSubmitting) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                            color = NeonGreen
                        )
                    } else {
                        Text("Finalizar compra")
                    }
                }
            }
        }
    ) { innerPadding ->

        // Padding de lista: respetamos el padding del Scaffold y damos espacio extra para la bottomBar
        val listPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + 120.dp
        )

        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()             // evita que el teclado tape los campos
                .navigationBarsPadding(), // evita superposición con la nav bar
            contentPadding = listPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                OutlinedTextField(
                    value = ui.name, onValueChange = onName,
                    label = { Text("Nombre completo") },
                    isError = "name" in ui.errors,
                    modifier = Modifier.fillMaxWidth()
                )
                Helper(ui.errors["name"])
            }

            item {
                OutlinedTextField(
                    value = ui.phone, onValueChange = onPhone,
                    label = { Text("Teléfono (+56)") },
                    isError = "phone" in ui.errors,
                    modifier = Modifier.fillMaxWidth()
                )
                Helper(ui.errors["phone"])
            }

            item {
                OutlinedTextField(
                    value = ui.address, onValueChange = onAddress,
                    label = { Text("Dirección") },
                    isError = "address" in ui.errors,
                    modifier = Modifier.fillMaxWidth()
                )
                Helper(ui.errors["address"])
            }

            item {
                LocationButton(
                    onLocationReady = { lat, lng ->
                        onSetLocation(lat, lng)
                        scope.launch {
                            val addr = reverseGeocode(ctx, lat, lng)
                            if (addr != null) {
                                onAddress(addr)
                            } else {
                                onAddress("$lat, $lng")
                                snackbarHostState.showSnackbar("No se pudo resolver la dirección; usando coordenadas.")
                            }
                        }
                    }
                )
            }

            item {
                DeliveryDropdown(
                    selected = ui.delivery,
                    onSelect = onDelivery
                )
            }

            item {
                if (ui.delivery == "Retiro en tienda") {
                    StoreMapButton(
                        store = StoreLocations.MAIN,
                        label = "Ver tienda y cómo llegar"
                    )
                }
            }

            item {
                PaymentDropdown(
                    selected = ui.payment,
                    onSelect = onPayment
                )
            }
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
// SELECT: PAGO (oscuro personalizado)
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