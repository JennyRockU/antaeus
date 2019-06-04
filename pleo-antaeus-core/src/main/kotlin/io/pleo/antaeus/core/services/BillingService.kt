package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.services.InvoiceService

class BillingService(private val paymentProvider: PaymentProvider, private val invoiceService: InvoiceService) {
    
   // TODO - Add code e.g. here
    
   fun chargeInvoices() {

        // TODO handle localization
        val date = java.time.LocalDate.now()
        val day = date.getDayOfMonth()

        // bill only on the first day of the month
        val isBillingPeriod = day == 1

        if (isBillingPeriod) {

            // get all the pending invoices
            var invoicesToProcess = invoiceService.fetch()
            var pending: List<Invoice> = invoicesToProcess
                .filter { it.status == InvoiceStatus.PENDING.toString() }

            // TODO try-catch and retry charge
            for (invoice in invoicesToProcess) {

                // TODO pass relevant parameters to 'charge' (customer, currency, amount)
                var charged = false
                try {

                    charged = PaymentProvider.charge(invoice)
                    if (charged) {

                        //TODO handle expetions/retry (customer should not be charged without
                        // the invoice to be marked as 'paid'
                        // mark invoice as 'PAID'
                        invoiceService.update(invoice.id)
                    }

                } catch (e: CustomerNotFoundException) {
                    // TODO call logger
                } catch (e: CurrencyMismatchException) {
                    // TODO call logger
                } catch (e: NetworkException) {
                    // TODO call logger
                } catch (e: Exception) {
                    // TODO call logger
                }

            }
        }

    }
    
}
