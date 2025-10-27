package com.example.levelupmobile.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
    Button(
        onClick = { openStoreInMaps(ctx, store) },
        modifier = modifier
    ) {
        Text(label)
    }
}
