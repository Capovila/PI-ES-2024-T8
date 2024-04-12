package br.com.projetopi.smartlock

import com.google.android.gms.maps.model.LatLng

//Data class que define os lugares que exitem armarios no mapa e suas informacoes
data class Place (
    val name: String,
    val latLng: LatLng,
    val address: String,
    val reference: String
)