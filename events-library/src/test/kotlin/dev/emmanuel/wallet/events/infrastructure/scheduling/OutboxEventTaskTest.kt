package dev.emmanuel.wallet.events.infrastructure.scheduling

import dev.emmanuel.wallet.WithMockExtension
import dev.emmanuel.wallet.events.application.usecase.PublishPendingOutboxEvents
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import net.javacrumbs.shedlock.core.LockAssert
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class OutboxEventTaskTest : WithMockExtension {

    @MockK(relaxed = true)
    private lateinit var publishPendingOutboxEvents: PublishPendingOutboxEvents

    @InjectMockKs
    private lateinit var task: OutboxEventTask

    @Test
    fun `should publish pending outbox event`() {
        every { LockAssert.assertLocked() } returns Unit

        task.publishPendingOutboxEvents()

        verify { publishPendingOutboxEvents.execute() }
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun setUp() {
            mockkStatic(LockAssert::class)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            unmockkStatic(LockAssert::class)
        }
    }

}