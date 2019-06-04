package io.pleo.antaeus.core.exceptions

class BankAccountNotFoundException(id: Int) : EntityNotFoundException("BankAccount", id)