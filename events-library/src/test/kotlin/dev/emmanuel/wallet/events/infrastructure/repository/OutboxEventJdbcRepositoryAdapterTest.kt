package dev.emmanuel.wallet.events.infrastructure.repository

import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PublishedOutboxEvent
import dev.emmanuel.wallet.events.mocks.OutboxEvents
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import java.time.Instant

@Import(value = [OutboxEventJdbcRepositoryAdapter::class])
class OutboxEventJdbcRepositoryAdapterTest : WithRepositoryTest() {

    @Autowired
    private lateinit var outboxEventJdbcRepositoryAdapter: OutboxEventJdbcRepositoryAdapter

    @Test
    fun `should save a pending outbox event`() {
        val pendingEvent = givenPendingOutboxEvent()

        val pendingSavedEvent = outboxEventJdbcRepositoryAdapter.save(pendingEvent) as PendingOutboxEvent

        assertThat(pendingSavedEvent)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(pendingEvent)

        assertThat(pendingSavedEvent.id).isNotNull()
    }

    @Test
    fun `should return the most recent pending outbox events`() {
        (1..10).map { givenPublishedOutboxEvent() }.forEach(outboxEventJdbcRepositoryAdapter::save)

        val pendingEvents = (1..10)
            .map(this::givenPendingOutboxEvent)
            .map { outboxEvent ->
                outboxEventJdbcRepositoryAdapter.save(outboxEvent)
                outboxEvent.event
            }

        val returnedEvents = outboxEventJdbcRepositoryAdapter
            .findMostRecentPendingEvents(10)
            .map { it.event }

        assertThat(returnedEvents)
            .hasSize(10)
            .containsExactlyElementsOf(pendingEvents)
    }

    private fun givenPendingOutboxEvent(eventSuffix: Int = 0) = OutboxEvents.givenPendingOutboxEvent(
        id = null,
        event = "test.event.$eventSuffix",
    )

    private fun givenPublishedOutboxEvent() = OutboxEvents.givenPublishedOutboxEvent(id = null)
}