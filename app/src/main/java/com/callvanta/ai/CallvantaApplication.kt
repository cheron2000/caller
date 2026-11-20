package com.callvanta.ai

import android.app.Application
import com.callvanta.ai.data.rules.ScreeningRuleStore
import com.callvanta.ai.domain.screening.ScreeningRule
import java.util.concurrent.atomic.AtomicReference

class CallvantaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ruleSnapshot.set(ScreeningRuleStore(this).loadRules())
    }

    companion object {
        private val ruleSnapshot = AtomicReference<List<ScreeningRule>>(emptyList())

        fun rulesSnapshot(): List<ScreeningRule> = ruleSnapshot.get()

        fun replaceRules(rules: List<ScreeningRule>) {
            ruleSnapshot.set(rules.toList())
        }
    }
}
