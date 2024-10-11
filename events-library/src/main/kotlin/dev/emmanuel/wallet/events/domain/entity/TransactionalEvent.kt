package dev.emmanuel.wallet.events.domain.entity

interface TransactionalEvent : ApplicationEvent {
    val idempotentKey: String
}