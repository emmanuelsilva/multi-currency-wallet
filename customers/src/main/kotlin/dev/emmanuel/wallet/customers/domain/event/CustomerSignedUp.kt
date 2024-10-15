package dev.emmanuel.wallet.customers.domain.event

import dev.emmanuel.wallet.customers.domain.entity.Customer
import java.time.Instant

data class CustomerSignedUp(
    override val topic: String = "customer.onboarding",
    override val event: String = "customer.signed.up",
    override val customer: Customer,
    override val idempotentKey: String = "${event}-${customer.id.value}-${customer.createdAt.epochSecond}",
) : CustomerEvent