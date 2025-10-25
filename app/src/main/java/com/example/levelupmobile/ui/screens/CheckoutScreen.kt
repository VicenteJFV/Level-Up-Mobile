package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.components.InputField
import com.example.levelupmobile.ui.theme.*
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onFinish: () -> Unit, onBack: () -> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val nameError = name.isBlank()
    val emailError = !email.contains("@") || !email.contains(".")
    val addressError = address.length < 5
    val formValid = !nameError && !emailError && !addressError

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("💳 Checkout", style = MaterialTheme.typography.titleLarge, color = ElectricBlue)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground,
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                    border = ButtonDefaults.outlinedButtonBorder
                ) { Text("Volver") }

                AppButton(
                    text = "Finalizar pedido",
                    onClick = { if (formValid) onFinish() },
                    modifier = Modifier.weight(1f),
                    type = ButtonType.Secondary
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InputField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre completo",
                placeholder = "Ej: Felipe Villalobos",
                isError = nameError,
                errorMessage = if (nameError) "Obligatorio" else null
            )
            InputField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                placeholder = "ejemplo@duoc.cl",
                isError = emailError,
                errorMessage = if (emailError) "Correo inválido" else null
            )
            InputField(
                value = address,
                onValueChange = { address = it },
                label = "Dirección",
                placeholder = "Calle y número",
                isError = addressError,
                errorMessage = if (addressError) "Dirección muy corta" else null
            )

            Text(
                text = if (formValid) "✅ Listo para pagar" else "Completa los campos para continuar",
                style = MaterialTheme.typography.bodyLarge,
                color = if (formValid) NeonGreen else LightGrayText
            )
        }
    }
}
