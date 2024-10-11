package dev.emmanuel.wallet.customers.domain.event

import dev.emmanuel.wallet.customers.mocks.Customers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CustomerSignedUpTest {

    @Test
    fun `should set the customer onboarding as default topic`() {
        val event = CustomerSignedUp(customer = Customers.givenCustomer())
        assertThat(event.topic).isEqualTo("customer.onboarding")
    }

    @Test
    fun `should set the customer signed up event as default event`() {
        val event = CustomerSignedUp(customer = Customers.givenCustomer())
        assertThat(event.event).isEqualTo("customer.signed.up")
    }

    @Test
    fun `should set the customer id as event key`() {
        val customer = Customers.givenCustomer()
        val event = CustomerSignedUp(customer = customer)
        assertThat(event.key).isEqualTo(customer.id.value.toString())
    }

    @Test
    fun `should generate a idempotent key`() {
        val customer = Customers.givenCustomer()
        val event = CustomerSignedUp(customer = customer)
        assertThat(event.idempotentKey).startsWith("customer.signed.up-${customer.id.value}")
    }

}