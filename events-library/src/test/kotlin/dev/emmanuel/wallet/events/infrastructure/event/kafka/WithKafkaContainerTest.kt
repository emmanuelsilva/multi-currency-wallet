package dev.emmanuel.wallet.events.infrastructure.event.kafka

import dev.emmanuel.wallet.events.domain.entity.Event
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.*

@SpringBootTest(
    classes = [
        KafkaEventPublisher::class,
        KafkaAutoConfiguration::class
    ]
)
@Testcontainers
class WithKafkaContainerTest {

    private var receivedMessages: MutableList<Event> = Collections.synchronizedList(mutableListOf<Event>())

    @BeforeEach
    fun setUp() {
        receivedMessages.clear()
    }

    fun consumingMessagesFromTopic(vararg topics: String) {
        val consumerProps = Properties()
        consumerProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
        consumerProps[ConsumerConfig.GROUP_ID_CONFIG] = "test-consumer"
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumer = KafkaConsumer(consumerProps, StringDeserializer(), JsonDeserializer(Map::class.java) )
        consumer.subscribe(listOf(*topics))

        Thread.startVirtualThread {
            while (true) {
                val records = consumer.poll(java.time.Duration.ofMillis(100))

                val receivedEvents = records.map {
                    Event(
                        topic = it.topic(),
                        key = it.key(),
                        payload = it.value()
                    )
                }

                receivedMessages.addAll(receivedEvents)
            }
        }
    }

    fun receivedMessages() = receivedMessages.toList()


    companion object {

        @Container
        val kafkaContainer: KafkaContainer = KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"))
            .withReuse(true)

        @JvmStatic
        @DynamicPropertySource
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            kafkaContainer.start()
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
        }
    }


}