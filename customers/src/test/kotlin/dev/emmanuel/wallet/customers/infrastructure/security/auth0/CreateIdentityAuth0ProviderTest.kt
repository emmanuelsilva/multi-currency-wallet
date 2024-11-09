package dev.emmanuel.wallet.customers.infrastructure.security.auth0

import dev.emmanuel.wallet.WithMockExtension
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test

class CreateIdentityAuth0ProviderTest : WithMockExtension {

    @MockK
    private lateinit var configuration: Auth0Configuration

    @InjectMockKs
    private lateinit var createIdentityAuth0Provider: IdentityAuth0ProviderCreator

    fun setUp() {

    }

    @Test
    fun `should create an identity in auth0 provider`() {

    }

    @Test
    fun `should wrap an auth0 failure into a ProviderException`() {

    }
}