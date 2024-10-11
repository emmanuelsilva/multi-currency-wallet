package dev.emmanuel.wallet.events.infrastructure.event

import dev.emmanuel.wallet.events.application.event.TransactionalEventPublisher
import dev.emmanuel.wallet.events.application.repository.OutboxEventRepository
import dev.emmanuel.wallet.events.domain.entity.PendingOutboxEvent
import dev.emmanuel.wallet.events.domain.entity.TransactionalEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxTransactionEventPublisher(val repository: OutboxEventRepository) : TransactionalEventPublisher {

    @Transactional
    override fun publish(transactionalEvent: TransactionalEvent) {
        val outboxEvent = PendingOutboxEvent.from(transactionalEvent)
        repository.save(outboxEvent)
    }
}