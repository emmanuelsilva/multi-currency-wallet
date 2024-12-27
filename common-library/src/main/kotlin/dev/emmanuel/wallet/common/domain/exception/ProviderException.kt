package dev.emmanuel.wallet.common.domain.exception

open class ProviderException(
    val name: String,
    override val message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)