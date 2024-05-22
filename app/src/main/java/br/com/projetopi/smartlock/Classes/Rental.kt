package br.com.projetopi.smartlock.Classes

/***
 * Essa classe representa uma locação
 * de um armário
 */
data class Rental(
    var managerId: String?,
    var uid: String?,
    val idUser: String?,
    val idPlace: String?,
    val tempoSelected: String?,
    var rentalImplemented: Boolean = false,
    var rentalOpen: Boolean = true,
    val establishmentManagerName: String?,
    var usersQuantity: String? = "0",
    var user1Photo: String? = null,
    var user2Photo: String? = null,
)