package com.callvanta.ai.domain.screening

/**
 * Caller identity information supplied to the policy engine.
 *
 * Missing means the screening callback did not provide usable identity metadata;
 * it is deliberately different from a known withheld or unknown caller ID.
 */
sealed interface CallerIdentity {
    data class Number(val normalized: String) : CallerIdentity {
        init {
            require(normalized.isNotBlank()) { "normalized number must not be blank" }
        }
    }

    data object Withheld : CallerIdentity
    data object Unknown : CallerIdentity
    data object Missing : CallerIdentity
}

enum class ScreeningAction {
    ALLOW,
    SILENCE,
    REJECT
}

sealed interface CallerMatcher {
    val specificity: Int

    data class ExactNumber(val normalized: String) : CallerMatcher {
        init {
            require(normalized.isNotBlank()) { "exact number must not be blank" }
        }
        override val specificity: Int = 3
    }

    data class NumberPrefix(val prefix: String) : CallerMatcher {
        init {
            require(prefix.isNotBlank()) { "number prefix must not be blank" }
        }
        override val specificity: Int = 2
    }

    data object WithheldOrUnknown : CallerMatcher {
        override val specificity: Int = 1
    }
}

data class ScreeningRule(
    val id: String,
    val priority: Int,
    val creationOrder: Long,
    val enabled: Boolean,
    val matcher: CallerMatcher,
    val action: ScreeningAction
)

data class ScreeningDecision(
    val action: ScreeningAction,
    val matchedRuleId: String? = null,
    val reason: String
)

/**
 * Deterministic, in-memory policy evaluation. This class has no Android,
 * storage, network, or AI dependencies and is safe to exercise with JVM tests.
 */
class ScreeningPolicy {
    fun evaluate(
        caller: CallerIdentity,
        rules: List<ScreeningRule>?,
        isSavedContact: Boolean = false
    ): ScreeningDecision {
        if (rules == null) {
            return allow("Policy data unavailable; fail open")
        }
        if (isSavedContact) {
            return allow("Saved contact allowed by default")
        }
        if (caller == CallerIdentity.Missing) {
            return allow("Caller identity metadata missing; fail open")
        }

        val matchingRule = rules.asSequence()
            .filter { it.enabled }
            .filter { matches(it.matcher, caller) }
            .sortedWith(
                compareByDescending<ScreeningRule> { it.priority }
                    .thenByDescending { it.matcher.specificity }
                    .thenBy { it.creationOrder }
            )
            .firstOrNull()

        return if (matchingRule == null) {
            allow("No rule matched; default allow")
        } else {
            ScreeningDecision(
                action = matchingRule.action,
                matchedRuleId = matchingRule.id,
                reason = "Matched rule ${matchingRule.id}"
            )
        }
    }

    private fun matches(matcher: CallerMatcher, caller: CallerIdentity): Boolean =
        when {
            matcher is CallerMatcher.ExactNumber && caller is CallerIdentity.Number ->
                caller.normalized == matcher.normalized
            matcher is CallerMatcher.NumberPrefix && caller is CallerIdentity.Number ->
                caller.normalized.startsWith(matcher.prefix)
            matcher == CallerMatcher.WithheldOrUnknown ->
                caller == CallerIdentity.Withheld || caller == CallerIdentity.Unknown
            else -> false
        }

    private fun allow(reason: String) = ScreeningDecision(
        action = ScreeningAction.ALLOW,
        reason = reason
    )
}
