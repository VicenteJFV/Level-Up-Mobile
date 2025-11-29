package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelupmobile.ui.theme.BlackBackground
import com.example.levelupmobile.ui.theme.BlackCard
import com.example.levelupmobile.ui.theme.ElectricBlue
import com.example.levelupmobile.ui.theme.LightGrayText
import com.example.levelupmobile.ui.theme.NeonGreen

@Composable
fun OrderSuccessScreen(
    orderId: Long,
    onGoHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícono de éxito
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = NeonGreen,
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Título
        Text(
            "¡Compra Exitosa!",
            color = ElectricBlue,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Tu pedido ha sido registrado.",
            color = LightGrayText,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // Tarjeta con ID centrado
        Card(
            colors = CardDefaults.cardColors(containerColor = BlackCard),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ID DE PEDIDO",
                    color = LightGrayText,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "#$orderId",
                    color = NeonGreen,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Mensaje de advertencia
        Card(
            colors = CardDefaults.cardColors(containerColor = BlackCard),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "⚠️ Recuerda guardar el ID de tu pedido para modificaciones o cancelaciones durante las próximas 24 hrs.",
                color = LightGrayText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // UN SOLO BOTÓN: Volver al Inicio
        Button(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonGreen,
                contentColor = BlackBackground
            )
        ) {
            Icon(Icons.Default.Home, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Volver al Inicio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}