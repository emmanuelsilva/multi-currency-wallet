package dev.emmanuel.wallet.events.infrastructure.event

import dev.emmanuel.wallet.events.application.repository.OutboxEventRepository
import dev.emmanuel.wallet.events.domain.entity.TransactionalEvent
import dev.emmanuel.wallet.events.infrastructure.repository.OutboxEventJdbcRepositoryAdapter
import dev.emmanuel.wallet.events.infrastructure.repository.WithRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@Import(value = [OutboxTransactionEventPublisher::class, OutboxEventJdbcRepositoryAdapter::class])
class OutboxTransactionEventPublisherTest : WithRepositoryTest() {

    @Autowired
    private lateinit var repository: OutboxEventRepository

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var publisher: OutboxTransactionEventPublisher

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun `should rollback the transaction when failed to save outbox event`() {
        val event = SimpleTransactionalEvent()

        assertThatThrownBy {
            transactionTemplate.executeWithoutResult {
                publisher.publish(event)
                throw RuntimeException()
            }
        }

        val pendingEvents = repository.findMostRecentPendingEvents(1)
        assertThat(pendingEvents).isEmpty()
    }

    @Test
    fun `should save an outbox event for a transactional event`() {
        val event = SimpleTransactionalEvent()
        publisher.publish(event)

        val pendingEvents = repository.findMostRecentPendingEvents(1)
        assertThat(pendingEvents).hasSize(1)
    }

    data class SimpleTransactionalEvent(
        override val topic: String = "test.topic",
        override val idempotentKey: String = "test.key",
        override val key: String? = "test.key",
        override val event: String = "test.event",
        val customProperty: String = "customProperty123"
    ) : TransactionalEvent

}