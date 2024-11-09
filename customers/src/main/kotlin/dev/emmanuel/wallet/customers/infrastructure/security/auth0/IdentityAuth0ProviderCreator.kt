package dev.emmanuel.wallet.customers.infrastructure.security.auth0

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.client.mgmt.filter.UserFilter
import com.auth0.json.auth.TokenHolder
import com.auth0.json.mgmt.users.User
import dev.emmanuel.wallet.common.domain.exception.ProviderException
import dev.emmanuel.wallet.customers.application.gateway.IdentityProviderCreator
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProviderRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private const val AUTH0_DEFAULT_CONNECTION = "Username-Password-Authentication"

@Component
class IdentityAuth0ProviderCreator(
    private val configuration: Auth0Configuration
) : IdentityProviderCreator {

    private val logger = KotlinLogging.logger { }

    override fun create(request: CreateIdentityProviderRequest) {
        val accessToken = getManagementAccessToken()
        val managementApi = ManagementAPI.newBuilder(configuration.domain, accessToken).build()
        val user = createUserFrom(request)

        try {
            managementApi.users().create(user).execute()
            logger.info { "Identity was created in the Auth0 provider for customer ${request.customer.id}" }
        } catch (e: Exception) {
            logger.error(e) { "Failure to create identity for customer ${request.customer.id} due to ${e.message}" }
            throw ProviderException("auth0", "Failed to create identity on auth0 provider", e)
        }
    }

    private fun createUserFrom(request: CreateIdentityProviderRequest): User {
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
        val holder = tokenRequest.execute().body

        return holder.accessToken
    }
}

fun main() {
    val authAPI = AuthAPI.newBuilder("dev-bpww3zfggex68i7p.us.auth0.com", "3oHCLjnvm2zYB2iOVhTznsmM08qoinQn", "0q66eJIDOt59TskeBwb6Gysu0gVi0GLozCDXkfWcvn1n4CmXqVbCP3zarlB_Q7UN").build()
    val tokenRequest = authAPI.requestToken("https://dev-bpww3zfggex68i7p.us.auth0.com/api/v2/")
    val holder: TokenHolder = tokenRequest.execute().body
    val accessToken = holder.accessToken
    println(accessToken)

    val managementApi = ManagementAPI.newBuilder("dev-bpww3zfggex68i7p.us.auth0.com", accessToken).build()
    val x = managementApi.users().list(UserFilter())
    println(x.execute().body.items.joinToString(",") { it.name })

    val user = User()
    user.id = "9F07D233-75B9-4C27-B3CB-D4AC95048CE1"
    user.username = "9F07D233-75B9-4C27-B3CB-D4AC95048CE1"
    user.name = "test via api"
    user.email = "test@emmanuel.net"
    user.isEmailVerified = true
    //user.setPassword("GoodPassword!02".toCharArray())
    user.setConnection(AUTH0_DEFAULT_CONNECTION)
    managementApi.users().create(user).execute()


    /*
    val formData: MultiValueMap<String, String> = LinkedMultiValueMap()
    formData.add("client_id", "3oHCLjnvm2zYB2iOVhTznsmM08qoinQn")
    formData.add("client_secret", "0q66eJIDOt59TskeBwb6Gysu0gVi0GLozCDXkfWcvn1n4CmXqVbCP3zarlB_Q7UN")
    formData.add("grant_type", "client_credentials")
    formData.add("audience", "https://dev-bpww3zfggex68i7p.us.auth0.com/api/v2/")

    val mgmtResult = RestClient
        .create()
        .post()
        .uri("https://dev-bpww3zfggex68i7p.us.auth0.com/oauth/token")
        .body(formData)
        .retrieve()
        .body(Map::class.java)

    println(mgmtResult)

    val managementApi = ManagementAPI.newBuilder("dev-bpww3zfggex68i7p.us.auth0.com", mgmtResult?.get("access_token")!! as String).build()
    val x = managementApi.users().list(UserFilter())
    println(x.execute().body.items.joinToString(",") { it.name })
    */
}