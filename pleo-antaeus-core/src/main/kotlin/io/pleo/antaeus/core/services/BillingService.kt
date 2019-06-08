package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import org.junit.jupiter.api.extension.ExtensionContextException
import java.time.LocalDate
import java.time.ZoneOffset

class BillingService(private val paymentProvider: PaymentProvider, private val invoiceService: InvoiceService) {

    var billingDayOfMonth = 1
    fun chargeInvoices() : BillingResult {

        val today = LocalDate.now(ZoneOffset.UTC)

        // bill only on the first day of the month
        val isBillingPeriod = today.dayOfMonth == billingDayOfMonth
        if (!isBillingPeriod){
            val msg = "Invoices are only charged on the 1st of each month. Today is ${today.month} ${today.dayOfMonth}."
            return BillingResult(message = msg)
        }

        // get all the pending invoices
        val invoicesToProcess = invoiceService.fetchAllOfStatus(InvoiceStatus.PENDING)

        // init result data
        val msg = "${invoicesToProcess.size} pending invoices were found"
        var processed = 0
        var failed = 0
        var rejected = 0

        // process all pending invoices
        for (invoice in invoicesToProcess) {
            // charge each invoice within a try-catch block to avoid failing
            // the rest of the invoices
            try {
               val success = chargeInvoice(invoice)

                if (success){
                    processed++

                } else{
                    rejected++
                }

            } catch (e: Exception){
                //TODO log or send errors
                failed++
            }
        }

        return BillingResult(message = msg, processed = processed, failed = failed, rejected = rejected)
    }

    internal fun chargeInvoice(invoice: Invoice): Boolean{
        var charged = try {
            paymentProvider.charge(invoice)

        } catch (e: NetworkException){
            //retry if failed for a network error
            paymentProvider.charge(invoice)
        }

        if (charged) {

            // mark the invoice as 'PAID'
            // it's crucial that a charged invoice won't remain 'pending', so attempt 3 times.
            updateBilledInvoice(invoice.id, retries = 3)
        }

        return charged
    }

    // attempts to charge the customer several times in case of a network exception
    // other exceptions will be propagated to the caller
    private fun updateBilledInvoice(invoiceId: Int, retries: Int){
        try {
            invoiceService.update(invoiceId, InvoiceStatus.PAID)

        } catch (e: Exception){

            var remaining = retries.minus(1)
            if (remaining < 0){
                // propagate the error if no retries left
                throw e
            }
            // if reached here, retry with reduced no. of tries.
            updateBilledInvoice(invoiceId, remaining)
        }
    }

    data class BillingResult(
            val message: String,
            val processed: Int = 0,
            val rejected: Int = 0,
            val failed: Int = 0
    )

}
