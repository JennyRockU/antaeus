package io.pleo.antaeus.models.bank

import java.math.BigDecimal


data class BankAccount(
    val id: Int,
    val balance: java.math.BigDecimal,
    val currency: Currency
)
