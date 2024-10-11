package dev.emmanuel.wallet.customers.application.repository

import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer

interface CustomerRepository {

    fun save(newCustomer: NewCustomer): Customer

    fun findByEmail(email: Email): Customer?

}