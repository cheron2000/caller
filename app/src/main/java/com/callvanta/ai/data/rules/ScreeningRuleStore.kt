package com.callvanta.ai.data.rules

import android.content.Context
import com.callvanta.ai.domain.screening.CallerMatcher
import com.callvanta.ai.domain.screening.ScreeningAction
import com.callvanta.ai.domain.screening.ScreeningRule
import org.json.JSONArray
import org.json.JSONObject

/**
 * Small local-only rule store. The service never reads SharedPreferences in its
 * screening callback; the application loads this store at process startup and
 * publishes an immutable snapshot for synchronous policy evaluation.
 */
class ScreeningRuleStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    fun loadRules(): List<ScreeningRule> {
        val raw = preferences.getString(KEY_RULES, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val matcher = when (item.optString("matcherType")) {
                        "exact" -> item.optString("matcherValue").takeIf(String::isNotBlank)
                            ?.let(CallerMatcher::ExactNumber)
                        "prefix" -> item.optString("matcherValue").takeIf(String::isNotBlank)
                            ?.let(CallerMatcher::NumberPrefix)
                        "withheld_unknown" -> CallerMatcher.WithheldOrUnknown
                        else -> null
                    } ?: continue
                    val action = runCatching {
                        ScreeningAction.valueOf(item.optString("action"))
                    }.getOrNull() ?: continue
                    val id = item.optString("id").takeIf(String::isNotBlank) ?: continue
                    add(
                        ScreeningRule(
                            id = id,
                            priority = item.optInt("priority", 0),
                            creationOrder = item.optLong("creationOrder", index.toLong()),
                            enabled = item.optBoolean("enabled", true),
                            matcher = matcher,
                            action = action
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveRules(rules: List<ScreeningRule>) {
        val array = JSONArray()
        rules.forEach { rule ->
            val matcherType: String
            val matcherValue: String
            when (val matcher = rule.matcher) {
                is CallerMatcher.ExactNumber -> {
                    matcherType = "exact"
                    matcherValue = matcher.normalized
                }
                is CallerMatcher.NumberPrefix -> {
                    matcherType = "prefix"
                    matcherValue = matcher.prefix
                }
                CallerMatcher.WithheldOrUnknown -> {
                    matcherType = "withheld_unknown"
                    matcherValue = ""
                }
            }
            array.put(
                JSONObject()
                    .put("id", rule.id)
                    .put("priority", rule.priority)
                    .put("creationOrder", rule.creationOrder)
                    .put("enabled", rule.enabled)
                    .put("matcherType", matcherType)
                    .put("matcherValue", matcherValue)
                    .put("action", rule.action.name)
            )
        }
        preferences.edit().putString(KEY_RULES, array.toString()).apply()
    }

    companion object {
        private const val PREFERENCES = "callvanta_local_rules"
        private const val KEY_RULES = "rules_json"
    }
}
