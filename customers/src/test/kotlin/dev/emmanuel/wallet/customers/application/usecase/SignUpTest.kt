package dev.emmanuel.wallet.customers.application.usecase

import dev.emmanuel.wallet.WithMockExtension
import dev.emmanuel.wallet.common.domain.exception.DomainException
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProvider
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProviderRequest
import dev.emmanuel.wallet.customers.application.repository.CustomerRepository
import dev.emmanuel.wallet.customers.domain.entity.*
import dev.emmanuel.wallet.customers.domain.event.CustomerSignedUp
import dev.emmanuel.wallet.customers.factory.Customers
import dev.emmanuel.wallet.events.application.event.TransactionalEventPublisher
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class SignUpTest : WithMockExtension {

    @MockK
    private lateinit var repository: CustomerRepository

    @RelaxedMockK
    private lateinit var transactionalEventPublisher: TransactionalEventPublisher

    @RelaxedMockK
    private lateinit var createIdentityProvider: CreateIdentityProvider

    @InjectMockKs
    private lateinit var signUp: SignUp

    @Test
    fun `should save customer, create identity and publish signed up event when a valid new customer was provided`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)

        val customer = givenSavedCustomer()
        val newCustomer = givenNewCustomer(email)
        val password = Password("Super@Password02")

        val request = SignUpRequest(
            newCustomer = newCustomer,
            password = password
        )

        val savedCustomer = signUp.execute(request)

        assertThatFindByEmailWasCalled(newCustomer)
        assertThatNewCustomerWasSaved(newCustomer)
        assertThat(savedCustomer).isEqualTo(customer)
        assertThatIdentityWasCreated(customer, password)
        assertCustomerSignedUpEventWasPublished(savedCustomer)
    }

    @Test
    fun `should return an DomainException when email already exists on database`() {
        val email = "email@example.com"
        givenExistingCustomer(email)

        val newCustomer = givenNewCustomer(email)
        val password = Password("Super@Password02")

        val request = SignUpRequest(
            newCustomer = newCustomer,
            password = password
        )

        assertThatThrownBy { signUp.execute(request) }
            .isInstanceOf(DomainException::class.java)
            .hasMessage("The given email address already exists")

        assertThatCustomerWasNotSaved(newCustomer)
        assertCustomerSignedUpEventWasNotPublished()
    }

    @Test
    fun `should return an error when failed to create identity`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)
        givenSavedCustomer()
        every { createIdentityProvider.create(any()) } throws RuntimeException("Failed to create provider identity")

        val newCustomer = givenNewCustomer(email)
        val password = Password("Super@Password02")

        val request = SignUpRequest(
            newCustomer = newCustomer,
            password = password
        )

        assertThatThrownBy { signUp.execute(request) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Failed to create provider identity")

        assertCustomerSignedUpEventWasNotPublished()
    }

    @Test
    fun `should return an error when failed to save customer and not publish signed up event`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)
        every { repository.save(any()) } throws RuntimeException("Failed to save")

        val newCustomer = givenNewCustomer(email)
        val password = Password("Super@Password02")

        val request = SignUpRequest(
            newCustomer = newCustomer,
            password = password
        )

        assertThatThrownBy { signUp.execute(request) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Failed to save")

        assertCustomerSignedUpEventWasNotPublished()
    }

    @Test
    fun `should return an error when failed to publish signed up event`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)
        every { transactionalEventPublisher.publish(any()) } throws RuntimeException("Failed to publish event")

        givenSavedCustomer()
        val newCustomer = givenNewCustomer(email)
        val password = Password("Super@Password02")

        val request = SignUpRequest(
            newCustomer = newCustomer,
            password = password
        )

        assertThatThrownBy { signUp.execute(request) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Failed to publish event")
    }

    private fun givenNonExistingEmailOnDatabase(email: String) {
        every { repository.findByEmail(Email(email)) } returns null
    }

    private fun assertThatNewCustomerWasSaved(newCustomer: NewCustomer) {
        verify { repository.save(newCustomer) }
    }

    private fun assertThatFindByEmailWasCalled(newCustomer: NewCustomer) {
        verify { repository.findByEmail(newCustomer.email) }
    }

    private fun givenNewCustomer(email: String) = NewCustomer(
        fullName = FullName("Sample Customer"),
        email = Email(email),
    )

    private fun givenSavedCustomer() : Customer {
        val customer = Customers.givenCustomer()
        every { repository.save(any()) } returns customer

        return customer
    }

    private fun givenExistingCustomer(email: String) {
        val customer = Customers.givenCustomer()
        every { repository.findByEmail(Email(email)) } returns customer
    }

    private fun assertCustomerSignedUpEventWasPublished(customer: Customer) {
        verify { transactionalEventPublisher.publish(CustomerSignedUp(customer = customer)) }
    }

    private fun assertThatCustomerWasNotSaved(newCustomer: NewCustomer) {
        verify(exactly = 0) { repository.save(newCustomer) }
    }

    private fun assertCustomerSignedUpEventWasNotPublished() {
        verify(exactly = 0) { transactionalEventPublisher.publish(any()) }
    }

    private fun assertThatIdentityWasCreated(customer: Customer, password: Password) {
        verify {
            createIdentityProvider.create(
                CreateIdentityProviderRequest(
                    customer = customer,
                    password = password
                )
            )
        }
    }
}