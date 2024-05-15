package br.com.projetopi.smartlock.Classes

import com.google.android.gms.maps.model.LatLng

/***
 * Essa classe representa um estabelecimento
 * que realiza locações de armários
 * (onde os armários ficam)
 */
class Establishment (
    val uid: String?,
    val name: String?,
    val latLng: LatLng?,
    val address: String?,
    val reference: String?,
    val managerName: String?
)