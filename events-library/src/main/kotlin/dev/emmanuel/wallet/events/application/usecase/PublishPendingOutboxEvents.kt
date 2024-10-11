package dev.emmanuel.wallet.events.application.usecase

import dev.emmanuel.wallet.common.application.usecase.UseCase
import dev.emmanuel.wallet.events.application.event.EventPublisher
import dev.emmanuel.wallet.events.application.repository.OutboxEventRepository
import dev.emmanuel.wallet.events.domain.entity.Event
import dev.emmanuel.wallet.events.domain.entity.OutboxEvent
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import io.github.oshai.kotlinlogging.KotlinLogging

@UseCase
class PublishPendingOutboxEvents(
    private val repository: OutboxEventRepository,
    private val eventPublisher: EventPublisher
) {
    private val logger = KotlinLogging.logger { }

    fun execute() {
        val pendingEvents = repository.findMostRecentPendingEvents(10)

        for (pendingEvent in pendingEvents) {
            logger.info { "Publishing event: $pendingEvent" }
            val modifiedEvent = publishEvent(pendingEvent)
            repository.save(modifiedEvent)
        }
    }

    private fun publishEvent(pendingEvent: PendingOutboxEvent): OutboxEvent {
        try {
            eventPublisher.publish(toEvent(pendingEvent))
            logger.info { "Event ${pendingEvent.id} was successfully published" }
            return pendingEvent.asPublished()
        } catch (ex: Exception) {
            logger.error(ex) { "Error publishing event" }
            return pendingEvent.fail(ex.message ?: "Unknown")
        }
    }

    private fun toEvent(outboxEvent: PendingOutboxEvent) = Event(
        topic = outboxEvent.topic,
        key = outboxEvent.key,
        payload = outboxEvent.payload
    )
}