package dev.emmanuel.wallet.customers.mocks

import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.CustomerID
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.FullName
import java.time.Instant

object Customers {

    fun givenCustomer(
        id: CustomerID = Customer.generateId(),
        fullName: FullName = FullName("Full Name"),
        email: Email = Email("mock@email.com"),
        createdAt: Instant = Instant.now()
    ) = Customer(
        id = id,
        fullName = fullName,
        email = email,
        createdAt = createdAt
    )
}