package com.example.levelupmobile.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder
import com.example.levelupmobile.common.StoreLocation

/**
 * Abre Google Maps si está instalado. Sino, abre en el navegador.
 */
fun openStoreInMaps(context: Context, store: StoreLocation) {
    val label = if (store.address.isNotBlank()) store.address else store.name
    val encodedLabel = URLEncoder.encode(label, "UTF-8")

    // 1) Intent para la app de Google Maps (si está instalada)
    val mapsUri = Uri.parse("geo:${store.lat},${store.lng}?q=${store.lat},${store.lng}($encodedLabel)")
    val mapsIntent = Intent(Intent.ACTION_VIEW, mapsUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(mapsIntent)
        return
    } catch (_: ActivityNotFoundException) {
        // Sigue al fallback
    }

    // 2) Fallback: abrir en navegador (Google Maps Web)
    val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${store.lat},${store.lng}&query_place_id=$encodedLabel")
    val webIntent = Intent(Intent.ACTION_VIEW, webUri)
    try {
        context.startActivity(webIntent)
    } catch (_: Exception) {
        // Nada más que hacer; opcional: mostrar un toast/snackbar si quieres
    }
}
