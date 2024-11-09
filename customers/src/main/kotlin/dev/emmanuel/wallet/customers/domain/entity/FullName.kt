package dev.emmanuel.wallet.customers.domain.entity

import dev.emmanuel.wallet.customers.domain.entity.validation.WithValidation
import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength

@JvmInline
value class FullName(val value: String) : WithValidation<FullName> {

    init {
        validate(this)
    }

    override fun toString() = value

    override fun validationDefinition() = Validation {
        FullName::value {
            addConstraint("Full name must not be blank") { it.isNotBlank() }
            addConstraint("Full name must contain at least first and last names") { it.trim().split(" ").size >= 2 }
            maxLength(255) hint "Full name must not be greater than 255 characters"
        }
    }
}
