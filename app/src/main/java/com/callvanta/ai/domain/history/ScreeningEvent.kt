package com.callvanta.ai.domain.history

import com.callvanta.ai.domain.screening.ScreeningAction

/** Minimal metadata for one incoming call observed by Callvanta's screening callback. */
data class ScreeningEvent(
    val id: String,
    val occurredAtEpochMillis: Long,
    val callerNumber: String?,
    val action: ScreeningAction
)

/** Pure retention policy so expiry behavior can be tested without Android dependencies. */
object ScreeningEventRetention {
    const val RETENTION_DAYS = 30L
    const val DAY_MILLIS = 24L * 60L * 60L * 1000L
    const val RETENTION_MILLIS = RETENTION_DAYS * DAY_MILLIS

    fun retain(events: List<ScreeningEvent>, nowEpochMillis: Long): List<ScreeningEvent> {
        val cutoff = nowEpochMillis - RETENTION_MILLIS
        return events.filter { it.occurredAtEpochMillis >= cutoff }
            .sortedByDescending { it.occurredAtEpochMillis }
    }
}
