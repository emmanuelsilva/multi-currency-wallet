package dev.emmanuel.wallet.customers.application.usecase

import dev.emmanuel.wallet.common.application.usecase.TransactionalUseCase
import dev.emmanuel.wallet.common.domain.exception.DomainException
import dev.emmanuel.wallet.customers.application.gateway.IdentityProviderCreator
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProviderRequest
import dev.emmanuel.wallet.customers.application.repository.CustomerRepository
import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
import dev.emmanuel.wallet.customers.domain.entity.Password
import dev.emmanuel.wallet.customers.domain.event.CustomerSignedUp
import dev.emmanuel.wallet.events.application.event.TransactionalEventPublisher
import io.github.oshai.kotlinlogging.KotlinLogging

data class SignUpRequest(
    val newCustomer: NewCustomer,
    val password: Password
)

@TransactionalUseCase
class SignUp(
    private val repository: CustomerRepository,
    private val identityProviderCreator: IdentityProviderCreator,
    private val transactionalEventPublisher: TransactionalEventPublisher
) {
    private val logger = KotlinLogging.logger {  }

    fun execute(request: SignUpRequest) : Customer {
        validateEmailUniqueness(request)

        val customer = saveCustomer(request)
        createIdentity(customer, request)
        publishEvent(customer)

        return customer
    }

    private fun saveCustomer(request: SignUpRequest): Customer {
        val customer = repository.save(request.newCustomer)
        logger.info { "Saved new customer ${customer.id}" }
        return customer
    }

    private fun createIdentity(customer: Customer, request: SignUpRequest) {
        val createIdentityRequest = CreateIdentityProviderRequest(
            customer = customer,
            password = request.password
        )

        identityProviderCreator.create(createIdentityRequest)
    }

    private fun validateEmailUniqueness(request: SignUpRequest) {
        val existingCustomer = repository.findByEmail(request.newCustomer.email)

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