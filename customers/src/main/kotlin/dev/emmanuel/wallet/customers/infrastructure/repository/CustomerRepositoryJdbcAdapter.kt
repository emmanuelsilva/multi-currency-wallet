package dev.emmanuel.wallet.customers.infrastructure.repository

import dev.emmanuel.wallet.customers.application.repository.CustomerRepository
import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryJdbcAdapter(
    private val repository: CustomerJdbcRepository
) : CustomerRepository {

    override fun save(newCustomer: NewCustomer): Customer {
        val entity = CustomerEntity.from(newCustomer)
        val savedCustomer = repository.save(entity)
        return savedCustomer.toDomain()
    }

    override fun findByEmail(email: Email): Customer? {
        return repository.findByEmail(email)?.let(CustomerEntity::toDomain)
    }
}