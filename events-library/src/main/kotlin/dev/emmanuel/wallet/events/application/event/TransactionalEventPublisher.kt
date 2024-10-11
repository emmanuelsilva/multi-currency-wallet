package dev.emmanuel.wallet.events.application.event

import dev.emmanuel.wallet.events.domain.entity.TransactionalEvent

interface TransactionalEventPublisher {

    fun publish(transactionalEvent: TransactionalEvent)
}