package dev.emmanuel.wallet.customers.application.usecase

import dev.emmanuel.wallet.common.application.usecase.TransactionalUseCase
import dev.emmanuel.wallet.common.domain.exception.DomainException
import dev.emmanuel.wallet.customers.application.repository.CustomerRepository
import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
import dev.emmanuel.wallet.customers.domain.event.CustomerSignedUp
import dev.emmanuel.wallet.events.application.event.TransactionalEventPublisher
import io.github.oshai.kotlinlogging.KotlinLogging

@TransactionalUseCase
class SignUp(
    private val repository: CustomerRepository,
    private val transactionalEventPublisher: TransactionalEventPublisher
) {
    private val logger = KotlinLogging.logger {  }

    fun execute(newCustomer: NewCustomer) : Customer {
        validateEmailUniqueness(newCustomer)
        val customer = save(newCustomer)
        publishEvent(customer)
        return customer
    }

    private fun save(newCustomer: NewCustomer): Customer {
        val customer = repository.save(newCustomer)
        logger.info { "Saved new customer ${customer.id}" }
        return customer
    }

    private fun validateEmailUniqueness(newCustomer: NewCustomer) {
        val existingCustomer = repository.findByEmail(newCustomer.email)

        if (existingCustomer != null) {
            throw DomainException("The given email address already exists")
        }
    }

    private fun publishEvent(customer: Customer) {
        val event = CustomerSignedUp(customer = customer)
        transactionalEventPublisher.publish(event)
        logger.info { "Customer signed up event published for customer ${customer.id}" }
    }

}