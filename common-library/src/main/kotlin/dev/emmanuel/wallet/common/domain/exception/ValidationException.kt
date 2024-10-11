package dev.emmanuel.wallet.common.domain.exception

open class ValidationException(
    val messages: List<String> = emptyList()
) : RuntimeException() {

    override val message: String?
        get() = messages.joinToString(prefix = "[", separator = ", ", postfix = "]")
}