package dev.emmanuel.wallet.customers.infrastructure.repository

import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.FullName
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import java.time.Instant
import java.time.temporal.ChronoUnit


@Import(value = [CustomerRepositoryJdbcAdapter::class])
class CustomerRepositoryJdbcAdapterTest : WithRepositoryTest() {

    @Autowired
    private lateinit var customerRepositoryJdbcAdapter: CustomerRepositoryJdbcAdapter

    @Test
    fun `should save a customer in the database`() {
        val newCustomer = NewCustomer(
            fullName = FullName("Full Name"),
            email = Email("email@example.com")
        )

        val customer = customerRepositoryJdbcAdapter.save(newCustomer)

        assertThat(customer).isNotNull()
        assertThat(customer.fullName).isEqualTo(newCustomer.fullName)
        assertThat(customer.email).isEqualTo(newCustomer.email)
        assertThat(customer.createdAt).isCloseTo(Instant.now(), within(10, ChronoUnit.SECONDS))
    }

    @Test
    fun `should return null when email does not exist`() {
        val customer = customerRepositoryJdbcAdapter.findByEmail(Email("not.found@example.com"))
        assertThat(customer).isNull()
    }

    @Test
    fun `should return the customer when finding by email`() {
        val email = Email("email@example.com")

        val newCustomer = NewCustomer(
            fullName = FullName("Full Name"),
            email = email
        )

        val savedCustomer = customerRepositoryJdbcAdapter.save(newCustomer)
        val customer = customerRepositoryJdbcAdapter.findByEmail(email)

        assertThat(customer)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("createdAt")
            .isEqualTo(savedCustomer)
    }

}