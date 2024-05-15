package br.com.projetopi.smartlock.Classes

/***
 * Essa classe representa uma locação
 * de um armário
 */
data class Rental (
    var uid: String?,
    val idUser: String?,
    val idPlace: String?,
    val tempoSelected: String?,
    var rentalImplemented: Boolean = false,
    var rentalOpen: Boolean = true,
    val establishmentManagerName: String?
)