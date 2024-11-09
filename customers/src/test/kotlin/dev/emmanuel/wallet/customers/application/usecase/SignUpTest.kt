package dev.emmanuel.wallet.customers.application.usecase

import dev.emmanuel.wallet.WithMockExtension
import dev.emmanuel.wallet.common.domain.exception.DomainException
import dev.emmanuel.wallet.customers.application.repository.CustomerRepository
import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.FullName
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
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

    @InjectMockKs
    private lateinit var signUp: SignUp

    @Test
    fun `should save and publish signed up event when a valid new customer was provided`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)

        val customer = givenSavedCustomer()
        val newCustomer = givenNewCustomer(email)

        val savedCustomer = signUp.execute(newCustomer)

        assertThatFindByEmailWasCalled(newCustomer)
        assertThatNewCustomerWasSaved(newCustomer)
        assertThat(savedCustomer).isEqualTo(customer)
        assertCustomerSignedUpEventWasPublished(savedCustomer)
    }

    @Test
    fun `should return an DomainException when email already exists on database`() {
        val email = "email@example.com"
        givenExistingCustomer(email)
        val newCustomer = givenNewCustomer(email)

        assertThatThrownBy { signUp.execute(newCustomer) }
            .isInstanceOf(DomainException::class.java)
            .hasMessage("The given email address already exists")

        assertThatCustomerWasNotSaved(newCustomer)
    }

    @Test
    fun `should return an error when failed to save customer and not publish signed up event`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)
        every { repository.save(any()) } throws RuntimeException("Failed to save")

        val newCustomer = givenNewCustomer(email)

        assertThatThrownBy { signUp.execute(newCustomer) }
            .isInstanceOf(RuntimeException::class.java)
            .hasMessage("Failed to save")

        verify(exactly = 0) { transactionalEventPublisher.publish(any()) }
    }

    @Test
    fun `should return an error when failed to publish signed up event`() {
        val email = "email@example.com"
        givenNonExistingEmailOnDatabase(email)
        every { transactionalEventPublisher.publish(any()) } throws RuntimeException("Failed to publish event")

        givenSavedCustomer()
        val newCustomer = givenNewCustomer(email)

        assertThatThrownBy { signUp.execute(newCustomer) }
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
        email = Email(email)
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
}