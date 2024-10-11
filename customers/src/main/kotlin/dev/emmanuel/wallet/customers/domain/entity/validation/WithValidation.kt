package dev.emmanuel.wallet.customers.domain.entity.validation

import dev.emmanuel.wallet.common.domain.exception.ValidationException
import io.konform.validation.Validation

interface WithValidation<T> {

    fun validationDefinition(): Validation<T>

    fun <T : WithValidation<T>> validate(entity: T) {
        val validationDefinition = entity.validationDefinition()
        val validationResult = validationDefinition(entity)

        if (!validationResult.isValid) {
            val errors = validationResult.errors
            val messages = errors.map { it.message }

            throw ValidationException(messages = messages)
        }
    }
}