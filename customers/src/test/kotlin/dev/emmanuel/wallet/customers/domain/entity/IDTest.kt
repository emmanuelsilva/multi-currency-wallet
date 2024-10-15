package dev.emmanuel.wallet.customers.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IDTest {

    @Test
    fun `should get correct value`() {
        val id = ID(1)
        assertThat(id.value).isEqualTo(1)
    }

    @Test
    fun `should delegate toString to value object`() {
        val id = ID("my-value")
        assertThat(id.toString()).isEqualTo("my-value")
    }
}