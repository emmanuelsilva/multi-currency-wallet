package dev.emmanuel.wallet.customers.domain.event

import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.events.domain.entity.TransactionalEvent

interface CustomerEvent : TransactionalEvent {
    override val key: String? get() = customer.id.value.toString()
    val customer: Customer
}