package dev.emmanuel.wallet.customers.application.gateway

import dev.emmanuel.wallet.customers.domain.entity.Customer
import dev.emmanuel.wallet.customers.domain.entity.Password

data class CreateIdentityProviderRequest(
    val customer: Customer,
    val password: Password
)

interface CreateIdentityProvider {

    fun create(request: CreateIdentityProviderRequest)
}