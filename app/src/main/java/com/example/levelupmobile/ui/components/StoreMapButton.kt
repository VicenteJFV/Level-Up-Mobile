package com.example.levelupmobile.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.levelupmobile.common.StoreLocation

@Composable
fun StoreMapButton(
    store: StoreLocation,
    modifier: Modifier = Modifier,
    label: String = "Ver tienda en Maps"
) {
    val ctx = LocalContext.current
    AppIconButton(
        text = label,
        icon = "🗺️",
        onClick = { openStoreInMaps(ctx, store) },
        modifier = modifier,
        type = ButtonType.Outlined
    )
}
