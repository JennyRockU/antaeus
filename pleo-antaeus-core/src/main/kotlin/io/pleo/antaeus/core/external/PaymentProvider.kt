/*
    This is the payment provider. It is a "mock" of an external service that you can pretend runs on another system.
    With this API you can ask customers to pay an invoice.

    This mock will succeed if the customer has enough money in their balance,
    however the documentation lays out scenarios in which paying an invoice could fail.
 */

package io.pleo.antaeus.core.external

import io.pleo.antaeus.models.Invoice

interface PaymentProvider {
    /*
        Charge a customer's account the amount from the invoice.

        Returns:
          `True` when the customer account was successfully charged the given amount.
          `False` when the customer account balance did not allow the charge.

        Throws:
          `CustomerNotFoundException`: when no customer has the given id.
          `CurrencyMismatchException`: when the currency does not match the customer account.
          `NetworkException`: when a network error happens.
     */

    fun charge(invoice: Invoice): Boolean {
        
        // get the currency of the customer
        var customer = customerService.fetch(invoice.customerId)

        // check if the customer currency matches the invoice currency
        if (customer.currency != invoice.amount.currency) {
            throw CurrencyMismatchException(invoice.id, customer.id)
        }

        // try to deduct the invoice amount from the customer (external) bank account
        // 'true' if successful, 'false' if no sufficient funds.
        return BankService.tryUpdate(customer.bankAccountId, invoice.amount)
    }
}
