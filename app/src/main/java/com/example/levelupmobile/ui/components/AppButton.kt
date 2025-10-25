package com.example.levelupmobile.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.levelupmobile.ui.theme.*

/**
 * Botón reutilizable con estilo Level-Up Gamer 🎮
 *
 * @param text Texto del botón
 * @param onClick Acción al presionar
 * @param modifier Permite ajustar el tamaño o padding
 * @param type Define el color del botón ("primary" = azul / "secondary" = verde)
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.Primary
) {
    val (bgColor, textColor) = when (type) {
        ButtonType.Primary -> ElectricBlue to BlackBackground
        ButtonType.Secondary -> NeonGreen to BlackBackground
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

enum class ButtonType { Primary, Secondary }
