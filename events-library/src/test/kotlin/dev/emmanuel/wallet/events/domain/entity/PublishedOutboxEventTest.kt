package dev.emmanuel.wallet.events.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class PublishedOutboxEventTest {

    @Test
    fun `show create from a pending event`() {
        val pendingEvent = PendingOutboxEvent(
            id = UUID.randomUUID(),
            topic = "test.topic",
            idempotentKey = "test.idempotent.key",
            key = "test.key",
            event = "test.event",
            payload = mapOf("test.outbox" to UUID.randomUUID()),
            retryCount = 5
        )

        val publishedEvent = PublishedOutboxEvent.from(pendingEvent)

        assertThat(publishedEvent)
            .usingRecursiveComparison()
            .ignoringFields("status", "publishedAt")
            .isEqualTo(pendingEvent)

        assertThat(publishedEvent.status).isEqualTo(OutboxEventStatus.PUBLISHED)
        assertThat(publishedEvent.publishedAt).isCloseTo(Instant.now(), within(5, ChronoUnit.SECONDS))
    }

}