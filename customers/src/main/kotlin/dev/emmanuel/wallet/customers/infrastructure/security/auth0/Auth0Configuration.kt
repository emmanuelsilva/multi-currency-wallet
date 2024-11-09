package dev.emmanuel.wallet.customers.infrastructure.security.auth0

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
data class Auth0Configuration(
    @Value("\${auth0.domain}") val domain: String,
    @Value("\${auth0.api.endpoint}") val apiEndpoint: String,
    @Value("\${okta.oauth2.client-id}") val clientId: String,
    @Value("\${okta.oauth2.client-secret}") val clientSecret: String
)