package dev.emmanuel.wallet.events.infrastructure.scheduling

import dev.emmanuel.wallet.events.application.usecase.PublishPendingOutboxEvents
import net.javacrumbs.shedlock.core.LockAssert
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxEventTask(
    private val publishPendingOutboxEvents: PublishPendingOutboxEvents
) {
    @Scheduled(cron = "*/5 * * * * *")
    @SchedulerLock(name = "publishPendingOutboxEventsTask")
    fun publishPendingOutboxEvents() {
        LockAssert.assertLocked()
        publishPendingOutboxEvents.execute()
    }

}