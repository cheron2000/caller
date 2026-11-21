package com.callvanta.ai.data.history

import android.content.Context
import com.callvanta.ai.domain.history.ScreeningEvent
import com.callvanta.ai.domain.history.ScreeningEventRetention
import com.callvanta.ai.domain.screening.ScreeningAction
import org.json.JSONArray
import org.json.JSONObject

/** Local-only event persistence. All methods are synchronized for service/UI coordination. */
class ScreeningEventStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    @Synchronized
    fun loadEvents(nowEpochMillis: Long = System.currentTimeMillis()): List<ScreeningEvent> {
        val raw = preferences.getString(KEY_EVENTS, null) ?: return emptyList()
        val loaded = runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val id = item.optString("id").takeIf(String::isNotBlank) ?: continue
                    val action = runCatching { ScreeningAction.valueOf(item.optString("action")) }.getOrNull() ?: continue
                    val timestamp = item.optLong("occurredAt", -1L)
                    if (timestamp < 0L) continue
                    add(
                        ScreeningEvent(
                            id = id,
                            occurredAtEpochMillis = timestamp,
                            callerNumber = item.optString("callerNumber").takeIf(String::isNotBlank),
                            action = action
                        )
                    )
                }
            }
        }.getOrElse { emptyList() }
        val retained = ScreeningEventRetention.retain(loaded, nowEpochMillis)
        if (retained != loaded) save(retained)
        return retained
    }

    @Synchronized
    fun append(event: ScreeningEvent, nowEpochMillis: Long = System.currentTimeMillis()) {
        val updated = ScreeningEventRetention.retain(loadEvents(nowEpochMillis) + event, nowEpochMillis)
        save(updated)
    }

    @Synchronized
    fun delete(eventId: String, nowEpochMillis: Long = System.currentTimeMillis()) {
        save(loadEvents(nowEpochMillis).filterNot { it.id == eventId })
    }

    @Synchronized
    fun deleteAll() {
        preferences.edit().remove(KEY_EVENTS).apply()
    }

    private fun save(events: List<ScreeningEvent>) {
        val array = JSONArray()
        events.forEach { event ->
            array.put(
                JSONObject()
                    .put("id", event.id)
                    .put("occurredAt", event.occurredAtEpochMillis)
                    .put("callerNumber", event.callerNumber)
                    .put("action", event.action.name)
            )
        }
        preferences.edit().putString(KEY_EVENTS, array.toString()).apply()
    }

    companion object {
        private const val PREFERENCES = "callvanta_screening_events"
        private const val KEY_EVENTS = "events_json"
    }
}
