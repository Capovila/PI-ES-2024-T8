package br.com.projetopi.smartlock.Classes

/***
 * Essa classe representa o usuário
 */
class User(
    var uid: String?,
    var name: String?,
    var email: String?,
    var password: String?,
    var age: Int,
    var CPF: String?,
    var phone: String?,
    var manager: Boolean? = false,
    var cardRegistred: Boolean = false
)


