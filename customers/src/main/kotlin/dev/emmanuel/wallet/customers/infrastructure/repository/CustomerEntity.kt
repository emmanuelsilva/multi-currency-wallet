package dev.emmanuel.wallet.customers.infrastructure.repository

import dev.emmanuel.wallet.customers.domain.entity.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table(name = "customers")
data class CustomerEntity(
    @Id val id: UUID? = null,
    @Column("full_name") val fullName: FullName,
    @Column("email") val email: Email,
    @Column("created_at") val createdAt: Instant
) {

    fun toDomain() = Customer(
        id = CustomerID(this.id!!),
        fullName = this.fullName,
        email = this.email,
        createdAt = this.createdAt
    )

    companion object {
        fun from(newCustomer: NewCustomer) = CustomerEntity(
            id = null,
            fullName = newCustomer.fullName,
            email = newCustomer.email,
            createdAt = Instant.now()
        )
    }
}
