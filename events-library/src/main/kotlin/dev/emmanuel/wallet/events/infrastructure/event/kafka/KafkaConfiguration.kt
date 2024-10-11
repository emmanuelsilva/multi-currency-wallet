package dev.emmanuel.wallet.events.infrastructure.event.kafka

import dev.emmanuel.wallet.events.domain.entity.ApplicationEvent
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfiguration {

    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, Map<*,*>> {
        val producerProperties = kafkaProperties.buildProducerProperties(null)
        return DefaultKafkaProducerFactory(producerProperties)
    }

    @Bean
    fun kafkaTemplate(kafkaProperties: KafkaProperties): KafkaTemplate<String, Map<*, *>> {
        return KafkaTemplate(producerFactory(kafkaProperties))
    }

}