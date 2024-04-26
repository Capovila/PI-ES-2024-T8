package br.com.projetopi.smartlock.Classes

class User(
    var uid: String?,
    var name: String?,
    val email: String?,
    var password: String?,
    var age: Int,
    var CPF: String?,
    var phone: String?,
    var manager: Boolean = false,
    var cardRegistred: Boolean = false
)


