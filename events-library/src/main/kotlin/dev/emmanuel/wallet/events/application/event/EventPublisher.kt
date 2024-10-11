package dev.emmanuel.wallet.events.application.event

import dev.emmanuel.wallet.events.domain.entity.Event

interface EventPublisher {

    fun publish(event: Event)

}