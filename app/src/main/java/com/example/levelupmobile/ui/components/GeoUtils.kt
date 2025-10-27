package com.example.levelupmobile.ui.components

import android.location.Geocoder
import android.os.Build
import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.util.Locale

/**
 * Devuelve una dirección legible para lat/lng o null si no se pudo.
 */
suspend fun reverseGeocode(
    context: Context,
    lat: Double,
    lng: Double
): String? {
    if (!Geocoder.isPresent()) return null

    val geocoder = Geocoder(context, Locale.getDefault())

    return if (Build.VERSION.SDK_INT >= 33) {
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lng, 1) { list ->
                val line = list?.firstOrNull()?.getAddressLine(0)
                if (cont.isActive) cont.resume(line)
            }
        }
    } else {
        @Suppress("DEPRECATION")
        try {
            val list = geocoder.getFromLocation(lat, lng, 1)
            list?.firstOrNull()?.getAddressLine(0)
        } catch (_: Throwable) {
            null
        }
    }
}
