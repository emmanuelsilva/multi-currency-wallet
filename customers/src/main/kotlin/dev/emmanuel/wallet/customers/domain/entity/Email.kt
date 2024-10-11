package dev.emmanuel.wallet.customers.domain.entity

import dev.emmanuel.wallet.customers.domain.entity.validation.WithValidation
import io.konform.validation.Validation

@JvmInline
value class Email(val value: String) : WithValidation<Email> {

    init {
        validate(this)
    }

    override fun validationDefinition() = Validation {
        Email::value {
            addConstraint("Email must not be blank") { it.isNotBlank() }
            addConstraint("Email must have a valid format") { emailRegexValidation.matches(it) }
        }
    }

    companion object {
        private val emailRegexValidation = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)".toRegex()
    }
}
