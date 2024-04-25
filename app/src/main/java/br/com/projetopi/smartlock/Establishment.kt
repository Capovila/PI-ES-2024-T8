package br.com.projetopi.smartlock

import com.google.android.gms.maps.model.LatLng

class Establishment (
    val uid: String?,
    val name: String?,
    val latLng: LatLng?,
    val address: String?,
    val reference: String?,
    val managerName: String?
)