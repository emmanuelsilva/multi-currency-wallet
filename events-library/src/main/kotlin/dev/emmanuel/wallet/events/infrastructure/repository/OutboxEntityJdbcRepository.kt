package dev.emmanuel.wallet.events.infrastructure.repository

import dev.emmanuel.wallet.events.domain.entity.OutboxEventStatus
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OutboxEntityJdbcRepository : CrudRepository<OutboxEventEntity, UUID> {

    @Query("select oe.* from outbox_events oe where oe.status = :status order by oe.created_at desc limit :limit")
    fun findTopEventsByStatus(status: OutboxEventStatus, limit: Int): List<OutboxEventEntity>
}