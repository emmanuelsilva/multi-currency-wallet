package dev.emmanuel.wallet.events.infrastructure.event.kafka

import dev.emmanuel.wallet.events.domain.entity.Event
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.concurrent.TimeUnit

class KafkaEventPublisherTest : WithKafkaContainerTest() {

    @Autowired
    private lateinit var kafkaEventPublisher: KafkaEventPublisher

    @Test
    fun `should publish an event with key to Kafka`() {
        val event = Event(
            topic = "my.key.topic",
            key = "test-key",
            payload = mapOf("event" to "test")
        )

        kafkaEventPublisher.publish(event)

        consumingMessagesFromTopic("my.key.topic")

        await()
            .pollInterval(Duration.ofMillis(100))
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                assertThat(receivedMessages()).containsExactly(event)
            }
    }

    @Test
    fun `should publish an event without key to Kafka`() {
        val event = Event(
            topic = "my.topic",
            key = null,
            payload = mapOf("event" to "test")
        )

        kafkaEventPublisher.publish(event)

        consumingMessagesFromTopic("my.topic")

        await()
            .pollInterval(Duration.ofMillis(100))
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                assertThat(receivedMessages()).containsExactly(event)
            }
    }
}