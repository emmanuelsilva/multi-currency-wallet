package dev.emmanuel.wallet.events.infrastructure.repository

import dev.emmanuel.wallet.events.application.repository.OutboxEventRepository
import dev.emmanuel.wallet.events.domain.entity.OutboxEvent
import dev.emmanuel.wallet.events.domain.entity.OutboxEventStatus
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import org.springframework.stereotype.Component

@Component
class OutboxEventJdbcRepositoryAdapter(private val repository: OutboxEntityJdbcRepository) : OutboxEventRepository {

    override fun save(outboxEvent: OutboxEvent): OutboxEvent {
        val entity = OutboxEventEntity.from(outboxEvent)
        val saved = repository.save(entity)
        return saved.toDomain()
    }

    override fun findMostRecentPendingEvents(limit: Int): List<PendingOutboxEvent> {
        return repository
            .findTopEventsByStatus(OutboxEventStatus.PENDING, limit)
            .map { entity -> entity.toDomain() as PendingOutboxEvent }
    }
}