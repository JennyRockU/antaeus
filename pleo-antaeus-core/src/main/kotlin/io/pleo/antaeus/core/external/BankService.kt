/*
    Implements endpoints related to an 'Extenal' service of the customer bank account
 */

package io.pleo.antaeus.core.extenal

import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.bank.BankAccount
import io.pleo.antaeus.core.exceptions.BankAccountNotFoundException

// BankService is an extenal service which represents customer's bank account
class BankService(private val dal: AntaeusDal) {

    fun tryUpdate(id: Int, amount: java.math.BigDecimal): Boolean {
        return dal.tryUpdateBankAccount(id, amount) ?: throw BankAccountNotFoundException(id)
    }
}
