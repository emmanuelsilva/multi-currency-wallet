package dev.emmanuel.wallet.events.domain.entity

import dev.emmanuel.wallet.common.Json
import java.time.Instant
import java.util.*

enum class OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED,
}

sealed interface OutboxEvent

data class PendingOutboxEvent(
    val id: UUID? = null,
    val topic: String,
    val idempotentKey: String,
    val key: String?,
    val event: String,
    val payload: Map<*, *>,
    val status: OutboxEventStatus = OutboxEventStatus.PENDING,
    val retryCount: Int = 0,
) : OutboxEvent {

    fun asPublished() = PublishedOutboxEvent.from(this)

    fun fail(reason: String) : OutboxEvent {
        return if (retryCount >= 10) {
            asFailed(reason)
        } else {
            incrementRetryCount()
        }
    }

    private fun asFailed(reason: String) = FailedOutboxEvent.from(this, reason)

    private fun incrementRetryCount() = copy(
        retryCount = retryCount + 1
    )

    companion object {
        fun from(transactionalEvent: TransactionalEvent) : PendingOutboxEvent = PendingOutboxEvent(
            id = null,
            topic = transactionalEvent.topic,
            idempotentKey = transactionalEvent.idempotentKey,
            key = transactionalEvent.key,
            event = transactionalEvent.event,
            payload = Json.toMap(transactionalEvent)
        )
    }
}

data class PublishedOutboxEvent(
    val id: UUID? = null,
    val topic: String,
    val idempotentKey: String,
    val key: String?,
    val event: String,
    val payload: Map<*, *>,
    val status: OutboxEventStatus = OutboxEventStatus.PUBLISHED,
    val retryCount: Int = 0,
    val publishedAt: Instant
) : OutboxEvent {
    companion object {
        fun from(pendingOutboxEvent: PendingOutboxEvent) = PublishedOutboxEvent(
            id = pendingOutboxEvent.id,
            topic = pendingOutboxEvent.topic,
            idempotentKey = pendingOutboxEvent.idempotentKey,
            key = pendingOutboxEvent.key,
            event = pendingOutboxEvent.event,
            payload = pendingOutboxEvent.payload,
            retryCount = pendingOutboxEvent.retryCount,
            publishedAt = Instant.now()
        )
    }
}

data class FailedOutboxEvent(
    val id: UUID? = null,
    val topic: String,
    val idempotentKey: String,
    val key: String?,
    val event: String,
    val payload: Map<*, *>,
    val status: OutboxEventStatus = OutboxEventStatus.FAILED,
    val retryCount: Int,
    val failedAt: Instant,
    val failedReason: String
) : OutboxEvent {

    companion object {
        fun from(pendingOutboxEvent: PendingOutboxEvent, reason: String) = FailedOutboxEvent(
            id = pendingOutboxEvent.id,
            topic = pendingOutboxEvent.topic,
            idempotentKey = pendingOutboxEvent.idempotentKey,
            key = pendingOutboxEvent.key,
            event = pendingOutboxEvent.event,
            payload = pendingOutboxEvent.payload,
            retryCount = pendingOutboxEvent.retryCount,
            failedAt = Instant.now(),
            failedReason = reason
        )
    }

}