package dev.emmanuel.wallet.customers.domain.entity

@JvmInline
value class ID<T>(val value: T) {
    override fun toString() = value.toString()
}


