package dev.emmanuel.wallet.customers.infrastructure.security.auth0

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.json.mgmt.users.User
import com.auth0.net.Response
import dev.emmanuel.wallet.WithMockExtension
import dev.emmanuel.wallet.customers.application.gateway.CreateIdentityProviderRequest
import dev.emmanuel.wallet.customers.domain.entity.Password
import dev.emmanuel.wallet.customers.factory.Customers
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CreateIdentityAuth0ProviderTest : WithMockExtension {

    @Test
    fun `should create an identity in auth0 provider`() {
        val configuration = Auth0Configuration(
            domain = "example.com",
            apiEndpoint = "http://localhost",
            clientId = "clientId",
            clientSecret = "clientSecret",
        )

        val managementApiMock = givenSuccessfullyCreateUserResponse()
        givenTokenFromAuthAPI(configuration)

        val createIdentityAuth0Provider = CreateIdentityAuth0Provider(configuration)

        val request = CreateIdentityProviderRequest(
            customer = Customers.givenCustomer(),
            password = Password("super@StrongPass#1234")
        )

        createIdentityAuth0Provider.create(request)

        verify {
            anyConstructed<ManagementAPI>()
                .users()
                .create( withArg { authRequest -> assertThatAuthRequestWasBuiltCorrectly(authRequest, request) })
                .execute()
        }

        confirmVerified(managementApiMock)
    }

    private fun assertThatAuthRequestWasBuiltCorrectly(
        authRequest: User,
        request: CreateIdentityProviderRequest
    ) {
        assertThat(authRequest.id).isEqualTo(request.customer.id.toString())
        assertThat(authRequest.username).isEqualTo(request.customer.id.toString())
        assertThat(authRequest.name).isEqualTo(request.customer.fullName.toString())
        assertThat(authRequest.email).isEqualTo(request.customer.email.toString())
        assertThat(authRequest.isEmailVerified).isTrue()
    }

    private fun givenTokenFromAuthAPI(configuration: Auth0Configuration) {
        mockkConstructor(AuthAPI::class)
        every {
            anyConstructed<AuthAPI>().requestToken(configuration.apiEndpoint).execute().body.accessToken
        } returns "test-access-token"
    }

    private fun givenSuccessfullyCreateUserResponse(): ManagementAPI {
        val managementApiMock = mockk<ManagementAPI>(relaxed = true)
        mockkConstructor(ManagementAPI::class)
        every { anyConstructed<ManagementAPI>().users().create(any()).execute() } returns mockk<Response<User>>()

        return managementApiMock
    }
}