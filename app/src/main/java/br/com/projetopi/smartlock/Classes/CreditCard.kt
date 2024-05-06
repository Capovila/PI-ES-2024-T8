package br.com.projetopi.smartlock.Classes

/***
 * Essa classe representa um cartão de crédito
 * do usuário
 */
class CreditCard(
    var userId: String?,
    var cardNumber: String?,
    var CVV: String?,
    var expireDate: String?,
    var cardName: String?
)