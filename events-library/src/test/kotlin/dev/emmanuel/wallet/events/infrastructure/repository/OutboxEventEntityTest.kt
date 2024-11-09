package dev.emmanuel.wallet.events.infrastructure.repository

import dev.emmanuel.wallet.events.domain.entity.FailedOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.OutboxEventStatus
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PublishedOutboxEvent
import dev.emmanuel.wallet.events.factory.OutboxEvents
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class OutboxEventEntityTest {

    @Test
    fun `should transform to pending outbox event`() {
        val outboxEventEntity = OutboxEventEntity(
            id = UUID.randomUUID(),
            idempotentKey = "idempotent.key",
            topic = "topic",
            key = "key",
            event = "event",
            payload = mapOf("foo" to "bar"),
            status = OutboxEventStatus.PENDING,
            retryCount = 1
        )

        val domain = outboxEventEntity.toDomain()
        assertThat(domain).isInstanceOf(PendingOutboxEvent::class.java)

        assertThat(domain)
            .usingRecursiveComparison()
            .isEqualTo(outboxEventEntity)
    }

    @Test
    fun `should transform to published outbox event`() {
        val outboxEventEntity = OutboxEventEntity(
            id = UUID.randomUUID(),
            idempotentKey = "idempotent.key",
            topic = "topic",
            key = "key",
            event = "event",
            payload = mapOf("foo" to "bar"),
            status = OutboxEventStatus.PUBLISHED,
            retryCount = 1,
            publishedAt = Instant.now()
        )

        val domain = outboxEventEntity.toDomain()
        assertThat(domain).isInstanceOf(PublishedOutboxEvent::class.java)

        assertThat(domain)
            .usingRecursiveComparison()
            .isEqualTo(outboxEventEntity)
    }

    @Test
    fun `should transform to failed outbox event`() {
        val outboxEventEntity = OutboxEventEntity(
            id = UUID.randomUUID(),
            idempotentKey = "idempotent.key",
            topic = "topic",
            key = "key",
            event = "event",
            payload = mapOf("foo" to "bar"),
            status = OutboxEventStatus.FAILED,
            retryCount = 1,
            failedAt = Instant.now(),
            failedReason = "Mock fail"
        )

        val domain = outboxEventEntity.toDomain()
        assertThat(domain).isInstanceOf(FailedOutboxEvent::class.java)

        assertThat(domain)
            .usingRecursiveComparison()
            .isEqualTo(outboxEventEntity)
    }

    @Test
    fun `should build from pending outbox event`() {
        val pendingOutboxEventEntity = OutboxEvents.givenPendingOutboxEvent()
        val entity = OutboxEventEntity.from(pendingOutboxEventEntity)

        assertThat(entity)
            .usingRecursiveComparison()
            .ignoringFields("publishedAt", "failedAt", "failedReason")
            .isEqualTo(pendingOutboxEventEntity)
    }

    @Test
    fun `should build from published outbox event`() {
        val publishedOutboxEventEntity = OutboxEvents.givenPublishedOutboxEvent()
        val entity = OutboxEventEntity.from(publishedOutboxEventEntity)

        assertThat(entity)
            .usingRecursiveComparison()
            .ignoringFields("failedAt", "failedReason")
            .isEqualTo(publishedOutboxEventEntity)
    }

    @Test
    fun `should build from failed outbox event`() {
        val failedOutboxEventEntity = OutboxEvents.givenFailedOutboxEvent()
        val entity = OutboxEventEntity.from(failedOutboxEventEntity)

        assertThat(entity)
            .usingRecursiveComparison()
            .ignoringFields("publishedAt")
            .isEqualTo(failedOutboxEventEntity)
    }
}