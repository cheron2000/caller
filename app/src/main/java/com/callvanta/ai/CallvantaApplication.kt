package com.callvanta.ai

import android.app.Application
import com.callvanta.ai.data.history.ScreeningEventStore
import com.callvanta.ai.data.rules.ScreeningRuleStore
import com.callvanta.ai.domain.history.ScreeningEvent
import com.callvanta.ai.domain.history.ScreeningEventRetention
import com.callvanta.ai.domain.screening.ScreeningRule
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class CallvantaApplication : Application() {
    private lateinit var eventStore: ScreeningEventStore
    private lateinit var eventWriter: ExecutorService

    override fun onCreate() {
        super.onCreate()
        ruleSnapshot.set(ScreeningRuleStore(this).loadRules())
        eventStore = ScreeningEventStore(this)
        eventSnapshot.set(eventStore.loadEvents())
        eventWriter = Executors.newSingleThreadExecutor()
    }

    fun screeningEventsSnapshot(): List<ScreeningEvent> = eventSnapshot.get()

    /** Update memory immediately; persist after the Telecom response has already been sent. */
    fun recordScreeningEvent(event: ScreeningEvent) {
        eventSnapshot.updateAndGet { current ->
            ScreeningEventRetention.retain(current + event, System.currentTimeMillis())
        }
        eventWriter.execute { eventStore.append(event) }
    }

    fun deleteScreeningEvent(eventId: String) {
        eventSnapshot.updateAndGet { current -> current.filterNot { it.id == eventId } }
        eventWriter.execute { eventStore.delete(eventId) }
    }

    fun deleteAllScreeningEvents() {
        eventSnapshot.set(emptyList())
        eventWriter.execute { eventStore.deleteAll() }
    }

    companion object {
        private val ruleSnapshot = AtomicReference<List<ScreeningRule>>(emptyList())
        private val eventSnapshot = AtomicReference<List<ScreeningEvent>>(emptyList())

        fun rulesSnapshot(): List<ScreeningRule> = ruleSnapshot.get()

        fun replaceRules(rules: List<ScreeningRule>) {
            ruleSnapshot.set(rules.toList())
        }
    }
}
