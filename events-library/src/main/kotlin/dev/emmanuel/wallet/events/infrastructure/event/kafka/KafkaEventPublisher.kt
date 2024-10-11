package dev.emmanuel.wallet.events.infrastructure.event.kafka

import dev.emmanuel.wallet.events.application.event.EventPublisher
import dev.emmanuel.wallet.events.domain.entity.ApplicationEvent
import dev.emmanuel.wallet.events.domain.entity.Event
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Map<*, *>>
) : EventPublisher {

    override fun publish(event: Event) {
        if (event.key != null) {
            kafkaTemplate.send(event.topic, event.key, event.payload)
        } else {
            kafkaTemplate.send(event.topic, event.payload)
        }
    }
}