package com.example.levelupmobile.ui.screens

import androidx.compose. foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material. icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx. compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import com.example.levelupmobile.ui. components.AppButton
import com. example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile. ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(
    orderId: Long,
    onGoHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Compra Exitosa",
                        style = MaterialTheme.typography. titleLarge.copy(
                            color = ElectricBlue
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppDimensions.paddingLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono de éxito
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Compra exitosa",
                tint = NeonGreen,
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier. height(AppDimensions.spacingLarge))

            // Título
            Text(
                "¡Compra Exitosa!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = ElectricBlue,
                    fontWeight = FontWeight. Bold
                )
            )

            Spacer(Modifier. height(AppDimensions.spacingSmall))

            // ✅ CAMBIO: Bold en texto descriptivo
            Text(
                "Tu pedido ha sido registrado.",
                style = MaterialTheme. typography.bodyLarge.copy(
                    color = WhiteText,
                    fontWeight = FontWeight.Bold  // ✅ Agregado
                ),
                textAlign = TextAlign. Center
            )

            Spacer(Modifier.height(AppDimensions.spacingXLarge))

            // Tarjeta con ID centrado
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults. cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .padding(AppDimensions.paddingLarge)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ID DE PEDIDO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = LightGrayText,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(Modifier.height(AppDimensions.spacingSmall))
                    Text(
                        "#$orderId",
                        style = MaterialTheme.typography. displayMedium.copy(
                            color = NeonGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }

            Spacer(Modifier.height(AppDimensions.spacingLarge))

            // Mensaje de advertencia
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier. fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(AppDimensions.paddingMedium)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⚠️",
                        style = MaterialTheme. typography.titleLarge,
                        modifier = Modifier.padding(end = AppDimensions.paddingSmall)
                    )
                    // ✅ CAMBIO: Bold en advertencia
                    Text(
                        "Recuerda guardar el ID de tu pedido para modificaciones o cancelaciones durante las próximas 24 hrs.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = WhiteText,
                            fontWeight = FontWeight.Bold  // ✅ Agregado
                        ),
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(Modifier.height(AppDimensions. spacingXLarge))

            // Botón usando AppButton
            AppButton(
                text = "Volver al Inicio",
                onClick = onGoHome,
                type = ButtonType.Primary,
                modifier = Modifier. fillMaxWidth()
            )
        }
    }
}