package com.example.levelupmobile.common

data class StoreLocation(
    val name: String,
    val lat: Double,
    val lng: Double,
    val address: String = ""
)

object StoreLocations {
    // Ejemplo: centro de Santiago (cámbialo por tu dirección real)
    val MAIN = StoreLocation(
        name = "Level-Up Gamer (Casa Matriz)",
        lat = -33.448918,
        lng = -70.6700713,
        address = "Duoc UC: Sede Alameda"
    )
}
