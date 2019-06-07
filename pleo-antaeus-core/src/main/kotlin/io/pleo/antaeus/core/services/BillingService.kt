package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.InvoiceStatus
import java.time.LocalDate
import java.time.ZoneOffset

class BillingService(private val paymentProvider: PaymentProvider, private val invoiceService: InvoiceService) {

    /*
    Throws:
    chargeInvoices can throw the following exceptions, which will be propagated to the caller (rest)
          `CustomerNotFoundException`: when no customer has the given id.
          `CurrencyMismatchException`: when the currency does not match the customer account.
          `NetworkException`: when a network error happens.
     */
    fun chargeInvoices() {
        val day = LocalDate.now(ZoneOffset.UTC).dayOfMonth

        // bill only on the first day of the month
        val isBillingPeriod = day == 1;
        if (isBillingPeriod) {

            // get all the pending invoices
            var invoicesToProcess = invoiceService.fetchAllOfStatus(InvoiceStatus.PENDING)

            for (invoice in invoicesToProcess) {

                var charged = paymentProvider.charge(invoice)
                if (charged) {

                    // mark the invoice as 'PAID'
                    // as it is crucial that a charged invoice will not remain 'pending', attempt 3 times.
                    updateBilledInvoice(invoice.id, retries = 3)
                }
            }
        }
    }

    private fun updateBilledInvoice(invoiceId: Int, retries: Int){
        try {
            invoiceService.update(invoiceId, InvoiceStatus.PAID)

        } catch (e: Exception){

            var remaining = retries.minus(1)
            if (remaining >= 0){
                updateBilledInvoice(invoiceId, remaining)
            }
        }
    }

}
