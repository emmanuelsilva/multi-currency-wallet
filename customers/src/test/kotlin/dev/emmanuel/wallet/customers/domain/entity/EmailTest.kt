package dev.emmanuel.wallet.customers.domain.entity

import dev.emmanuel.wallet.common.domain.exception.ValidationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class EmailTest {

    @Test
    fun `should return an error when passing an empty string`() {
        assertThatThrownBy { Email("") }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Email must not be blank, Email must have a valid format]")
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "invalid",
        "invalid@domain",
        "invalid.com",
    ])
    fun `should return an error when passing an invalid email`(invalidEmail: String) {
        assertThatThrownBy { Email(invalidEmail) }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Email must have a valid format]")
    }

    @Test
    fun `should return Email when provided a valid email address`() {
        val email = Email("address@domain.com")
        assertThat(email.value).isEqualTo("address@domain.com")
    }

}