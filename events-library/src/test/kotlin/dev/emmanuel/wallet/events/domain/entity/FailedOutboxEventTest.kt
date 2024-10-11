package dev.emmanuel.wallet.events.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

class FailedOutboxEventTest {

    @Test
    fun `should create from pending event`() {
        val pendingEvent = PendingOutboxEvent(
            id = UUID.randomUUID(),
            topic = "test.topic",
            idempotentKey = "test.idempotent.key",
            key = "test.key",
            event = "test.event",
            payload = mapOf("test.outbox" to UUID.randomUUID()),
            retryCount = 5
        )

        val failedEvent = FailedOutboxEvent.from(pendingEvent, "fail reason")

        assertThat(failedEvent)
            .usingRecursiveComparison()
            .ignoringFields("status", "failedAt", "failedReason")
            .isEqualTo(pendingEvent)

        assertThat(failedEvent.status).isEqualTo(OutboxEventStatus.FAILED)
        assertThat(failedEvent.failedAt).isCloseTo(Instant.now(), within(5, ChronoUnit.SECONDS))
        assertThat(failedEvent.failedReason).isEqualTo("fail reason")
    }

}