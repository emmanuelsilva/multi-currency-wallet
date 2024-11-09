package dev.emmanuel.wallet.customers.domain.entity

import dev.emmanuel.wallet.customers.domain.entity.validation.WithValidation
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.pattern

private const val requiredSpecialChars = "!@#$%^&*"

@JvmInline
value class Password(val raw: String) : WithValidation<Password> {

    init {
        validate(this)
    }

    override fun validationDefinition() = Validation {
        Password::raw {
            minLength(8) hint "Password must be at least 8 characters long"
            pattern("^[a-zA-Z0-9!@#$%^&*]+$") hint "Password can only contain letters, numbers, and special characters !@#$%^&*"
            addConstraint("Password must contain at least one special character !@#\$%^&*") { password ->
                password.any { it in requiredSpecialChars }
            }
        }
    }

}