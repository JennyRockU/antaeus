package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.extenal.BankService
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

class BillingServiceTest {

    // setup
    private val dal = mockk<AntaeusDal> {}
    private val invoiceService = InvoiceService(dal = dal)
    private val paymentProvider : PaymentProvider = object : PaymentProvider {
       override fun charge (invoice: Invoice) :Boolean{
            return true
        }
    }
    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

    // make sure there's no attempt to charge when not within billing day
    @Test
    fun `will not attempt to charge not on billing period`(){
        val today = LocalDate.now()
        val differentDay = if (today.dayOfMonth < 28) {today.dayOfMonth + 1} else { today.dayOfMonth - 1}
        billingService.billingDayOfMonth = differentDay

        var billingResult = billingService.chargeInvoices()
        val expectedMsg = "Invoices are only charged on the 1st of each month. Today is ${today.month} ${today.dayOfMonth}."
        assert(billingResult.message.contains(expectedMsg))
        println("is ok")
    }


    /**
     * To be tested with non-mocked database connection:
    */

    // make sure there's an attempt to charge on billing day
    @Test
    fun `will attempt to charge on billing period`(){

        val today = LocalDate.now()
        billingService.billingDayOfMonth = today.dayOfMonth

        var billingResult = billingService.chargeInvoices()
        val expectedMsgText = "pending invoices were found"
        assert(billingResult.message.contains(expectedMsgText))
    }

    // create invoice to charge
    private val customer = dal.createCustomer(currency = Currency.DKK)
    private val pendingInvoice = dal.createInvoice(Money(BigDecimal(27500), Currency.DKK),
            customer!!,
            InvoiceStatus.PENDING)

    // charge invoice and make sure that it's no longer pending
    @Test
    fun `will charge and update pending invoice`(){

        var testInvoice = dal.fetchInvoice(pendingInvoice!!.id)
        val today = LocalDate.now()
        billingService.billingDayOfMonth = today.dayOfMonth

        billingService.chargeInvoice(testInvoice!!)
        assert( testInvoice!!.status == InvoiceStatus.PAID)
    }

}