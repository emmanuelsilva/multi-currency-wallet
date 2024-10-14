package dev.emmanuel.wallet.events.infrastructure.event.kafka

import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest(
    classes = [
        KafkaEventPublisher::class,
        KafkaAutoConfiguration::class
    ]
)
@Testcontainers
class WithKafkaContainerTest {

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