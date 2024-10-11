package dev.emmanuel.wallet.customers.domain.entity

import dev.emmanuel.wallet.common.domain.exception.ValidationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class FullNameTest {

    @Test
    fun `should return an error when passing an empty string`() {
        assertThatThrownBy { FullName("") }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Full name must not be blank, Full name must contain at least first and last names]")
    }

    @Test
    fun `should return error when only first name was provided`() {
        assertThatThrownBy { FullName("Name") }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Full name must contain at least first and last names]")
    }

    @Test
    fun `should return error when it has more than 255 characters`() {
        val bigName = "a".repeat(255)

        assertThatThrownBy { FullName("First N${bigName}me") }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Full name must not be greater than 255 characters]")
    }

    @Test
    fun `should return FullName when provided at least first and last name`() {
        val fullName = FullName("Full Name")
        assertThat(fullName.value).isEqualTo("Full Name")
    }

}