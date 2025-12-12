package com.example.levelupmobile.ui.screens

import androidx. compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material. icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose. material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx. compose.ui.unit.sp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.theme.*

@Composable
fun OrderSuccessScreen(
    orderId: Long,
    onGoHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(AppDimensions.paddingXLarge),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment. CenterHorizontally
    ) {
        // Título principal
        Text(
            "Compra Exitosa",
            style = MaterialTheme.typography.headlineLarge.copy(
                color = ElectricBlue,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppDimensions.spacingLarge))

        // Ícono de éxito (verde neón)
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier. size(120.dp)
        )

        Spacer(Modifier.height(AppDimensions.spacingLarge))

        // Subtítulo
        Text(
            "¡Compra Exitosa!",
            style = MaterialTheme.typography.titleLarge.copy(
                color = ElectricBlue,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppDimensions.spacingSmall))

        Text(
            "Tu pedido ha sido registrado.",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = WhiteText,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier. height(AppDimensions.spacingXLarge))

        // Tarjeta con ID del pedido
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppDimensions. cornerRadius)
        ) {
            Column(
                modifier = Modifier
                    .padding(AppDimensions.paddingLarge)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ID DE PEDIDO",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = WhiteText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(Modifier.height(AppDimensions.spacingSmall))
                Text(
                    "#$orderId",
                    style = MaterialTheme. typography.displayLarge.copy(
                        color = NeonGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 48.sp
                    )
                )
            }
        }

        Spacer(Modifier. height(AppDimensions.spacingLarge))

        // Mensaje de advertencia
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppDimensions.cornerRadius)
        ) {
            Row(
                modifier = Modifier
                    .padding(AppDimensions.paddingMedium)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "⚠️",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = AppDimensions.paddingSmall)
                )
                Text(
                    "Recuerda guardar el ID de tu pedido para modificaciones o cancelaciones durante las próximas 24 hrs.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = WhiteText,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(Modifier.height(AppDimensions.spacingXLarge))

        // Botón "Volver al Inicio"
        AppButton(
            text = "Volver al Inicio",
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth(),
            type = ButtonType.Primary
        )
    }
}