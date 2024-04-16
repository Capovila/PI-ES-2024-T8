package br.com.projetopi.smartlock

class User(
    var uid: String?,
    var name: String?,
    val email: String?,
    var password: String?,
    var age: Int,
    var CPF: String?,
    var phone: String?,
    var cardNumber: Int? = null,
    var manager: Boolean = false)


