package dev.emmanuel.wallet.events.application.repository

import dev.emmanuel.wallet.events.domain.entity.OutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent

interface OutboxEventRepository {

    fun save(outboxEvent: OutboxEvent): OutboxEvent

    fun findMostRecentPendingEvents(limit: Int): List<PendingOutboxEvent>
}