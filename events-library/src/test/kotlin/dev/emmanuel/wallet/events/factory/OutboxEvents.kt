package dev.emmanuel.wallet.events.factory

import dev.emmanuel.wallet.events.domain.entity.FailedOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PublishedOutboxEvent
import java.time.Instant
import java.util.*

object OutboxEvents {

    fun givenPendingOutboxEvent(
        id: UUID? = UUID.randomUUID(),
        topic: String = "test.topic",
        idempotentKey: String = "test.idempotent.key",
        key: String = "test.key",
        event: String = "test.event",
        payload: Map<String, Any> = mapOf("test.outbox" to "test.value"),
        retryCount: Int = 1,
    ) = PendingOutboxEvent(
        id = id,
        topic = topic,
        idempotentKey = idempotentKey,
        key = key,
        event = event,
        payload = payload,
        retryCount = retryCount
    )

    fun givenPublishedOutboxEvent(
        id: UUID? = UUID.randomUUID(),
        topic: String = "test.topic",
        idempotentKey: String = "test.idempotent.key",
        key: String = "test.key",
        event: String = "test.event",
        payload: Map<String, Any> = mapOf("test.outbox" to "test.value"),
        retryCount: Int = 1,
        publishedAt: Instant = Instant.now(),
    ) = PublishedOutboxEvent(
        id = id,
        topic = topic,
        idempotentKey = idempotentKey,
        key = key,
        event = event,
        payload = payload,
        retryCount = retryCount,
        publishedAt = publishedAt
    )

    fun givenFailedOutboxEvent(
        id: UUID? = UUID.randomUUID(),
        topic: String = "test.topic",
        idempotentKey: String = "test.idempotent.key",
        key: String = "test.key",
        event: String = "test.event",
        payload: Map<String, Any> = mapOf("test.outbox" to "test.value"),
        retryCount: Int = 1,
        failedAt: Instant = Instant.now(),
        failedReason: String = "failed reason"
    ) = FailedOutboxEvent(
        id = id,
        topic = topic,
        idempotentKey = idempotentKey,
        key = key,
        event = event,
        payload = payload,
        retryCount = retryCount,
        failedAt = failedAt,
        failedReason = failedReason
    )

}