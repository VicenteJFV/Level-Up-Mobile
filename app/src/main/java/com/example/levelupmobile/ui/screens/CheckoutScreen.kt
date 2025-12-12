package com.example.levelupmobile.ui.screens

import androidx. compose.foundation.BorderStroke
import androidx.compose. foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy. items
import androidx.compose.material3.*
import androidx.compose. runtime.*
import androidx.compose. ui.Modifier
import androidx.compose.ui. platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose. ui.platform.LocalDensity
import androidx.compose.foundation.lazy.*
import androidx.compose.ui. platform.LocalView
import androidx.core.view.ViewCompat
import com.example.levelupmobile. common.StoreLocations
import com.example.levelupmobile.ui. components.AppButton
import com. example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.components. LocationButton
import com.example.levelupmobile.ui.components. StoreMapButton
import com.example.levelupmobile.ui. components.reverseGeocode
import com.example.levelupmobile.ui.theme.*
import com.example.levelupmobile.vm.CheckoutUi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    ui: CheckoutUi,
    onName:  (String) -> Unit,
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
                title = {
                    Text(
                        "💳 Checkout",
                        style = MaterialTheme.typography. titleLarge. copy(
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = BlackBackground,
        bottomBar = {
            // ✅ Botones sólidos sin transparencia
            Surface(
                modifier = Modifier. fillMaxWidth(),
                color = BlackBackground
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions. spacingMedium)
                ) {
                    AppButton(
                        text = "Volver",
                        onClick = onBack,
                        enabled = ! ui.isSubmitting,
                        type = ButtonType. Outlined
                    )

                    AppButton(
                        text = if (ui.isSubmitting) "..." else "Finalizar compra",
                        onClick = onSubmit,
                        enabled = ui.canSubmit && !ui.isSubmitting,
                        modifier = Modifier.weight(1f),
                        type = ButtonType. Primary
                    )
                }
            }
        }
    ) { innerPadding ->

        val listPadding = PaddingValues(
            start = AppDimensions.paddingLarge,
            end = AppDimensions.paddingLarge,
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding() + 16.dp
        )

        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding(),
            contentPadding = listPadding,
            verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingMedium)
        ) {

            item {
                OutlinedTextField(
                    value = ui.name,
                    onValueChange = onName,
                    label = { Text("Nombre completo") },
                    isError = "name" in ui.errors,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = LightGrayText,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = LightGrayText,
                        cursorColor = ElectricBlue,
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText
                    ),
                    modifier = Modifier. fillMaxWidth()
                )
                Helper(ui.errors["name"])
            }

            item {
                OutlinedTextField(
                    value = ui.phone,
                    onValueChange = onPhone,
                    label = { Text("Teléfono (+56)") },
                    isError = "phone" in ui.errors,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = LightGrayText,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = LightGrayText,
                        cursorColor = ElectricBlue,
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Helper(ui.errors["phone"])
            }

            item {
                OutlinedTextField(
                    value = ui.address,
                    onValueChange = onAddress,
                    label = { Text("Dirección") },
                    isError = "address" in ui. errors,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = LightGrayText,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = LightGrayText,
                        cursorColor = ElectricBlue,
                        focusedTextColor = WhiteText,
                        unfocusedTextColor = WhiteText
                    ),
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
        Text(
            msg,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

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
                cursorColor = WhiteText,
                focusedTextColor = WhiteText,
                unfocusedTextColor = WhiteText,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = SurfaceDark
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = WhiteText,
                            style = MaterialTheme.typography.bodyMedium. copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDropdown(selected: String, onSelect:  (String) -> Unit) {
    val options = listOf("Efectivo", "Débito", "Crédito")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = ! expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Método de pago", color = LightGrayText) },
            trailingIcon = { ExposedDropdownMenuDefaults. TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = LightGrayText,
                focusedLabelColor = ElectricBlue,
                unfocusedLabelColor = LightGrayText,
                cursorColor = WhiteText,
                focusedTextColor = WhiteText,
                unfocusedTextColor = WhiteText,
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = SurfaceDark
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            color = WhiteText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}