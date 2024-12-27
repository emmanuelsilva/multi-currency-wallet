package dev.emmanuel.wallet.customers.infrastructure.security.auth0

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.json.mgmt.users.User
import dev.emmanuel.wallet.common.domain.exception.ProviderException
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProvider
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProviderRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CreateIdentityAuth0Provider(
    private val configuration: Auth0Configuration
) : CreateIdentityProvider {

    private val logger = KotlinLogging.logger { }

    override fun create(request: CreateIdentityProviderRequest) {
        val accessToken = getManagementAccessToken()
        val managementApi = ManagementAPI.newBuilder(configuration.domain, accessToken).build()
        val user = buildAuth0UserFrom(request)

        try {
            managementApi.users().create(user).execute()
            logger.info { "Identity was created in the Auth0 provider for customer ${request.customer.id}" }
        } catch (e: Exception) {
            logger.error(e) { "Failure to create identity for customer ${request.customer.id} due to ${e.message}" }
            throw ProviderException("auth0", "Failed to register new customer", e)
        }
    }

    private fun buildAuth0UserFrom(request: CreateIdentityProviderRequest): User {
        val user = User()
        user.id = request.customer.id.toString()
        user.username = request.customer.id.toString()
        user.name = request.customer.fullName.toString()
        user.email = request.customer.email.toString()
        user.isEmailVerified = true
        user.setPassword(request.password.raw.toCharArray())
        user.setConnection(AUTH0_DEFAULT_CONNECTION)

        return user
    }

    private fun getManagementAccessToken(): String {
        val authAPI = AuthAPI
            .newBuilder(
                configuration.domain,
                configuration.clientId,
                configuration.clientSecret
            ).build()

        val tokenRequest = authAPI.requestToken(configuration.apiEndpoint)
        val tokenHolder = tokenRequest.execute().body

        return tokenHolder.accessToken
    }

    companion object {
        private const val AUTH0_DEFAULT_CONNECTION = "Username-Password-Authentication"
    }
}