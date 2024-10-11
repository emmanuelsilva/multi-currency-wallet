package dev.emmanuel.wallet.events.infrastructure.repository

import dev.emmanuel.wallet.events.domain.entity.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("outbox_events")
data class OutboxEventEntity(
    @Id val id: UUID? = null,
    @Column("idempotent_key") val idempotentKey: String,
    @Column("topic") val topic: String,
    @Column("key") val key: String?,
    @Column("event") val event: String,
    @Column("payload") val payload: Map<*, *>,
    @Column("status") val status: OutboxEventStatus,
    @Column("retry_count") val retryCount: Int = 0,
    @Column("published_at") val publishedAt: Instant? = null,
    @Column("failed_at") val failedAt: Instant? = null,
    @Column("failed_reason") val failedReason: String? = null,
) {
    fun toDomain() = when (this.status) {
        OutboxEventStatus.PENDING -> toPendingOutboxEvent()
        OutboxEventStatus.PUBLISHED -> toPublishedOutboxEvent()
        OutboxEventStatus.FAILED -> toFailedOutboxEvent()
    }

    private fun toPendingOutboxEvent() = PendingOutboxEvent(
        id = this.id,
        idempotentKey = this.idempotentKey,
        topic = this.topic,
        key = this.key,
        event = this.event,
        payload = this.payload,
        status = OutboxEventStatus.PENDING,
        retryCount = this.retryCount,
    )

    private fun toPublishedOutboxEvent() = PublishedOutboxEvent(
        id = this.id,
        idempotentKey = this.idempotentKey,
        topic = this.topic,
        key = this.key,
        event = this.event,
        payload = this.payload,
        status = OutboxEventStatus.PUBLISHED,
        retryCount = this.retryCount,
        publishedAt = this.publishedAt!!
    )

    private fun toFailedOutboxEvent() = FailedOutboxEvent(
        id = this.id,
        idempotentKey = this.idempotentKey,
        topic = this.topic,
        key = this.key,
        event = this.event,
        payload = this.payload,
        status = OutboxEventStatus.FAILED,
        retryCount = this.retryCount,
        failedAt = this.failedAt!!,
        failedReason = this.failedReason!!
    )

    companion object {
        fun from(outboxEvent: OutboxEvent) = when (outboxEvent) {
            is PendingOutboxEvent -> buildEntityFromPendingEvent(outboxEvent)
            is PublishedOutboxEvent -> buildEntityFromPublishedEvent(outboxEvent)
            is FailedOutboxEvent -> buildEntityFromFailedEvent(outboxEvent)
        }

        private fun buildEntityFromFailedEvent(failedEvent: FailedOutboxEvent) = OutboxEventEntity(
            id = failedEvent.id,
            idempotentKey = failedEvent.idempotentKey,
            topic = failedEvent.topic,
            key = failedEvent.key,
            event = failedEvent.event,
            payload = failedEvent.payload,
            status = failedEvent.status,
            retryCount = failedEvent.retryCount
        )

        private fun buildEntityFromPendingEvent(pendingEvent: PendingOutboxEvent) = OutboxEventEntity(
            id = pendingEvent.id,
            idempotentKey = pendingEvent.idempotentKey,
            topic = pendingEvent.topic,
            key = pendingEvent.key,
            event = pendingEvent.event,
            payload = pendingEvent.payload,
            status = pendingEvent.status,
            retryCount = pendingEvent.retryCount
        )

        private fun buildEntityFromPublishedEvent(publishedEvent: PublishedOutboxEvent) = OutboxEventEntity(
            id = publishedEvent.id,
            idempotentKey = publishedEvent.idempotentKey,
            topic = publishedEvent.topic,
            key = publishedEvent.key,
            event = publishedEvent.event,
            payload = publishedEvent.payload,
            status = publishedEvent.status,
            retryCount = publishedEvent.retryCount,
            publishedAt = publishedEvent.publishedAt
        )
    }
}