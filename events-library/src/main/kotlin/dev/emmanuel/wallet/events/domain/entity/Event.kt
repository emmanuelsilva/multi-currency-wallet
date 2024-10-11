package dev.emmanuel.wallet.events.domain.entity

data class Event(
    val topic: String,
    val key: String? = null,
    val payload: Map<*, *>
)
