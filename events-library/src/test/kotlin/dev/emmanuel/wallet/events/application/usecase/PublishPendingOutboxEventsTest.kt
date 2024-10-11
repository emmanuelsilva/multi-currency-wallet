package dev.emmanuel.wallet.events.application.usecase

import dev.emmanuel.wallet.WithMockExtension
import dev.emmanuel.wallet.events.application.event.EventPublisher
import dev.emmanuel.wallet.events.application.repository.OutboxEventRepository
import dev.emmanuel.wallet.events.domain.entity.Event
import dev.emmanuel.wallet.events.domain.entity.FailedOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PublishedOutboxEvent
import dev.emmanuel.wallet.events.mocks.OutboxEvents
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PublishPendingOutboxEventsTest : WithMockExtension {

    @RelaxedMockK
    private lateinit var repository: OutboxEventRepository

    @RelaxedMockK
    private lateinit var eventPublisher: EventPublisher

    @InjectMockKs
    private lateinit var publishPendingOutboxEvents: PublishPendingOutboxEvents

    @Test
    fun `should do nothing when there are no pending outbox events`() {
        every { repository.findMostRecentPendingEvents(any()) } returns emptyList()

        publishPendingOutboxEvents.execute()

        assertThatPendingEventsWereFetched()
        verify(exactly = 0) { repository.save(any()) }
        verify(exactly = 0) { eventPublisher.publish(any()) }
    }

    @Test
    fun `should mark pending event as published when successfully publishing all events`() {
        val pendingEvents = (1..10).map { OutboxEvents.givenPendingOutboxEvent() }.toList()
        every { repository.findMostRecentPendingEvents(any()) } returns pendingEvents

        publishPendingOutboxEvents.execute()

        assertThatPendingEventsWereFetched()

        for (pendingEvent in pendingEvents) {
            assertThatEventWasPublished(pendingEvent)
            assertThatPublishedEventWasSaved(pendingEvent)
        }
    }

    @Test
    fun `should mark pending event as published when successfully publishing the event`() {
        val pendingEvent =  OutboxEvents.givenPendingOutboxEvent()
        every { repository.findMostRecentPendingEvents(any()) } returns listOf(pendingEvent)

        publishPendingOutboxEvents.execute()

        assertThatPendingEventsWereFetched()
        assertThatEventWasPublished(pendingEvent)
        assertThatPublishedEventWasSaved(pendingEvent)
    }

    @Test
    fun `should increment retry count when publishing failed`() {
        val pendingEvent =  OutboxEvents.givenPendingOutboxEvent(retryCount = 1)
        every { repository.findMostRecentPendingEvents(any()) } returns listOf(pendingEvent)
        every { eventPublisher.publish(any()) } throws RuntimeException("publish failed")

        publishPendingOutboxEvents.execute()

        assertThatPendingEventsWereFetched()
        assertThatEventWasPublished(pendingEvent)

        assertThatRetryCountWasIncremented(pendingEvent, expectedRetryCount = 2)
    }

    @Test
    fun `should mark pending event as failed when publishing failed after 10 retries`() {
        val pendingEvent =  OutboxEvents.givenPendingOutboxEvent(retryCount = 10)
        every { repository.findMostRecentPendingEvents(any()) } returns listOf(pendingEvent)
        every { eventPublisher.publish(any()) } throws RuntimeException("publish failed")

        publishPendingOutboxEvents.execute()

        assertThatPendingEventsWereFetched()
        assertThatEventWasPublished(pendingEvent)
        assertThatFailedEventWasSaved(pendingEvent, failedReason = "publish failed")
    }

    @Test
    fun `should mark pending event as failed with unknown message when publishing failed after 10 retries but exception has not message`() {
        val pendingEvent =  OutboxEvents.givenPendingOutboxEvent(retryCount = 10)
        every { repository.findMostRecentPendingEvents(any()) } returns listOf(pendingEvent)
        every { eventPublisher.publish(any()) } throws RuntimeException()

        publishPendingOutboxEvents.execute()

        assertThatPendingEventsWereFetched()
        assertThatEventWasPublished(pendingEvent)
        assertThatFailedEventWasSaved(pendingEvent, failedReason = "Unknown")
    }

    private fun assertThatPendingEventsWereFetched() {
        verify { repository.findMostRecentPendingEvents(10) }
    }

    private fun assertThatPublishedEventWasSaved(pendingEvent: PendingOutboxEvent) {
        verify {
            repository.save(withArg {
                assertThat(it)
                    .isInstanceOf(PublishedOutboxEvent::class.java)
                    .hasFieldOrPropertyWithValue("id", pendingEvent.id)
            })
        }
    }

    private fun assertThatFailedEventWasSaved(pendingEvent: PendingOutboxEvent, failedReason: String) {
        verify {
            repository.save(withArg {
                assertThat(it)
                    .isInstanceOf(FailedOutboxEvent::class.java)
                    .hasFieldOrPropertyWithValue("id", pendingEvent.id)

                assertThat(it).hasFieldOrPropertyWithValue("failedReason", failedReason)
            })
        }
    }

    private fun assertThatRetryCountWasIncremented(pendingEvent: PendingOutboxEvent, expectedRetryCount: Int) {
        verify {
            repository.save(withArg {
                assertThat(it)
                    .isInstanceOf(PendingOutboxEvent::class.java)
                    .usingRecursiveComparison()
                    .ignoringFields("retryCount")
                    .isEqualTo(pendingEvent)

                assertThat(it).hasFieldOrPropertyWithValue("retryCount", expectedRetryCount)
            })
        }
    }

    private fun assertThatEventWasPublished(pendingEvent: PendingOutboxEvent) {
        verify {
            eventPublisher.publish(
                Event(
                    topic = pendingEvent.topic,
                    key = pendingEvent.key,
                    payload = pendingEvent.payload
                )
            )
        }
    }


}