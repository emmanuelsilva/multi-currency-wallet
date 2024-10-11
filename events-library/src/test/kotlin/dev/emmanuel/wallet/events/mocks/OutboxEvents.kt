package dev.emmanuel.wallet.events.mocks

import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
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

}