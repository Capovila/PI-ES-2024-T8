package br.com.projetopi.smartlock.Classes

data class Rental (
    var uid: String?,
    val idUser: String?,
    val idPlace: String?,
    val tempoSelected: String?,
    var rentalImplemented: Boolean = false,
    val establishmentManagerName: String?
)