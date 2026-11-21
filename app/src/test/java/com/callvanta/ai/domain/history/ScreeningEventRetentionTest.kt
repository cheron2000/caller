package com.callvanta.ai.domain.history

import com.callvanta.ai.domain.screening.ScreeningAction
import org.junit.Assert.assertEquals
import org.junit.Test

class ScreeningEventRetentionTest {
    private val now = 2_000_000_000_000L

    @Test
    fun removesEventsOlderThanThirtyDaysAndSortsNewestFirst() {
        val expired = event("expired", now - ScreeningEventRetention.RETENTION_MILLIS - 1L)
        val oldestKept = event("oldest", now - ScreeningEventRetention.RETENTION_MILLIS)
        val newest = event("newest", now - 1L)

        val result = ScreeningEventRetention.retain(listOf(newest, expired, oldestKept), now)

        assertEquals(listOf("newest", "oldest"), result.map { it.id })
    }

    @Test
    fun keepsFutureTimestampsAndEventsAtRetentionBoundary() {
        val boundary = event("boundary", now - ScreeningEventRetention.RETENTION_MILLIS)
        val future = event("future", now + 1000L)

        val result = ScreeningEventRetention.retain(listOf(boundary, future), now)

        assertEquals(listOf("future", "boundary"), result.map { it.id })
    }

    private fun event(id: String, timestamp: Long) = ScreeningEvent(
        id = id,
        occurredAtEpochMillis = timestamp,
        callerNumber = "9876543210",
        action = ScreeningAction.ALLOW
    )
}
