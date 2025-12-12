package com.example.levelupmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.levelupmobile.ui.theme.*

/**
 * Botón con ícono reutilizable con estilo Level-Up Gamer
 *
 * @param text Texto del botón
 * @param icon Emoji o ícono a mostrar
 * @param onClick Acción al presionar
 * @param modifier Permite ajustar el tamaño o padding
 * @param type Define el estilo del botón
 * @param size Define el tamaño del botón
 * @param enabled Habilita o deshabilita el botón
 */
@Composable
fun AppIconButton(
    text: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true
) {
    val (bgColor, textColor) = when (type) {
        ButtonType.Primary -> ElectricBlue to BlackBackground
        ButtonType.Secondary -> NeonGreen to BlackBackground
        ButtonType.Outlined -> BlackBackground to ElectricBlue
    }

    val height = when (size) {
        ButtonSize.Small -> AppDimensions.buttonHeightSmall
        ButtonSize.Medium -> AppDimensions.buttonHeightMedium
        ButtonSize.Large -> AppDimensions.buttonHeightLarge
    }

    val contentPadding = when (size) {
        ButtonSize.Small -> PaddingValues(horizontal = AppDimensions.paddingSmall, vertical = AppDimensions.paddingXSmall)
        ButtonSize.Medium -> PaddingValues(horizontal = AppDimensions.paddingMedium, vertical = AppDimensions.paddingSmall)
        ButtonSize.Large -> PaddingValues(horizontal = AppDimensions.paddingLarge, vertical = AppDimensions.paddingMedium)
    }

    when (type) {
        ButtonType.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = height),
                enabled = enabled,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ElectricBlue,
                    disabledContentColor = LightGrayText.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, if (enabled) ElectricBlue else LightGrayText.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(AppDimensions.cornerRadius),
                contentPadding = contentPadding
            ) {
                Text(text = icon)
                Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        else -> {
            Button(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = height),
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = bgColor,
                    contentColor = textColor,
                    disabledContainerColor = bgColor.copy(alpha = 0.4f),
                    disabledContentColor = textColor.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(AppDimensions.cornerRadius),
                contentPadding = contentPadding
            ) {
                Text(text = icon)
                Spacer(modifier = Modifier.width(AppDimensions.spacingSmall))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
