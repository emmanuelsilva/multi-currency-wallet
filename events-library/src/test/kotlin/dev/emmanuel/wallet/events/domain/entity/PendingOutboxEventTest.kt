package dev.emmanuel.wallet.events.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PendingOutboxEventTest {

    @Test
    fun `should build from transaction event`() {
        val transactionalEvent = TestTransactionalEvent()
        val pendingEvent = PendingOutboxEvent.from(transactionalEvent)

        assertThat(pendingEvent.id).isNull()
        assertThat(pendingEvent.topic).isEqualTo("test.topic")
        assertThat(pendingEvent.idempotentKey).isEqualTo("idempotent-key")
        assertThat(pendingEvent.key).isEqualTo("test.key")
        assertThat(pendingEvent.event).isEqualTo("test.event")
        assertThat(pendingEvent.payload).isEqualTo(
            mapOf(
                "customProperty" to "some-value",
                "event" to "test.event",
                "idempotentKey" to "idempotent-key"
            )
        )
        assertThat(pendingEvent.status).isEqualTo(OutboxEventStatus.PENDING)
        assertThat(pendingEvent.retryCount).isZero()
    }

    @Test
    fun `should transform to a published event`() {
        val transactionalEvent = TestTransactionalEvent()
        val pendingEvent = PendingOutboxEvent.from(transactionalEvent)
        val publishedEvent = pendingEvent.asPublished()

        assertThat(publishedEvent)
            .isInstanceOf(PublishedOutboxEvent::class.java)
            .usingRecursiveComparison()
            .ignoringFields("status", "publishedAt")
            .isEqualTo(pendingEvent)
    }

    @Test
    fun `should increment retry count when failing but retry count is less than 10`() {
        val transactionalEvent = TestTransactionalEvent()
        val pendingEvent = PendingOutboxEvent.from(transactionalEvent)

        val failedResult = pendingEvent.fail("fail reason") as PendingOutboxEvent

        assertThat(failedResult)
            .usingRecursiveComparison()
            .ignoringFields("retryCount")
            .isEqualTo(pendingEvent)

        assertThat(failedResult.retryCount).isEqualTo(1)
    }

    @Test
    fun `should transform to failed when failing but retry count is greater than 10`() {
        val transactionalEvent = TestTransactionalEvent()
        val pendingEvent = PendingOutboxEvent.from(transactionalEvent).copy(retryCount = 10)

        val failedResult = pendingEvent.fail("fail reason") as FailedOutboxEvent

        assertThat(failedResult)
            .usingRecursiveComparison()
            .ignoringFields("status", "failedAt", "failedReason")
            .isEqualTo(pendingEvent)

        assertThat(failedResult.failedReason).isEqualTo("fail reason")
    }

    private data class TestTransactionalEvent(
        override val topic: String = "test.topic",
        override val idempotentKey: String = "idempotent-key",
        override val key: String? = "test.key",
        override val event: String = "test.event",
        val customProperty: String = "some-value"
    ) : TransactionalEvent

}