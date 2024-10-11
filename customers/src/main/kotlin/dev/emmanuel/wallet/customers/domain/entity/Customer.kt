package dev.emmanuel.wallet.customers.domain.entity

import java.time.Instant
import java.util.*

typealias CustomerID = ID<UUID>

data class Customer(
    val id: CustomerID,
    val fullName: FullName,
    val email: Email,
    val createdAt: Instant
) {
    companion object {
        fun generateId(): CustomerID = ID(UUID.randomUUID())
    }
}

