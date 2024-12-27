package dev.emmanuel.wallet.customers.application.gateway

import dev.emmanuel.wallet.customers.domain.entity.AuthenticationToken

interface AuthenticationProvider {

    fun authenticate(username: String, password: String): AuthenticationToken
}