package dev.emmanuel.wallet.common.domain.exception

open class DomainException(
    override val message: String
) : RuntimeException(message)