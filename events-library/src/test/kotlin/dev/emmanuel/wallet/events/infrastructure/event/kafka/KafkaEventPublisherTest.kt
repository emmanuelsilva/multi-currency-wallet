package dev.emmanuel.wallet.events.infrastructure.event.kafka

import dev.emmanuel.wallet.events.domain.entity.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

class KafkaEventPublisherTest : WithKafkaContainerTest() {

    @Autowired
    private lateinit var kafkaEventPublisher: KafkaEventPublisher

    @Test
    fun `should publish an event to Kafka`() {
        val payload = mapOf(
            "event" to "test"
        )

        val event = Event(
            topic = "my.topic",
            key = "test-key",
            payload = payload
        )

        kafkaEventPublisher.publish(event)
    }

    companion object {

        @Container
        private val kafkaContainer = KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"))

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            kafkaContainer.start()
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
        }
    }
}