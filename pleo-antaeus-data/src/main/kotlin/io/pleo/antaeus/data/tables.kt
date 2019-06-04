/*
    Defines database tables and their schemas.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import org.jetbrains.exposed.sql.Table

object InvoiceTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val currency = varchar("currency", 3)
    val value = decimal("value", 1000, 2)
    val customerId = reference("customer_id", CustomerTable.id)
    val status = text("status")
}

object CustomerTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val currency = varchar("currency", 3)
    val bankAccountId = integer("id") // an extenal id number therefore not references the BankAccount table
}

// ***External***
// represents an extenal entity of a customer bank account
object BankAccountTable : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val balance = decimal("value", 100000, 2) //TODO check values
    val currency = varchar("currency", 3)
}

