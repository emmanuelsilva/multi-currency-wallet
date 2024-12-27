package dev.emmanuel.wallet.customers.domain.entity

import dev.emmanuel.wallet.common.domain.exception.ValidationException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PasswordTest {

    @Test
    fun `should create Password successfully for valid password`() {
        val password = Password("Valid@123")
        assertThat(password.raw).isEqualTo("Valid@123")
    }

    @Test
    fun `should throw exception if password is less than 8 characters`() {
        assertThatThrownBy { Password("Short1!") }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Password must be at least 8 characters long]")
    }

    @Test
    fun `should throw exception if password does not contain a special character`() {
        assertThatThrownBy { Password("NoSpecial123") }
            .isInstanceOf(ValidationException::class.java)
            .hasMessage("[Password must contain at least one special character !@#$%^&*]")
    }
}
