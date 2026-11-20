package com.callvanta.ai.domain.screening

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScreeningPolicyTest {
    private val policy = ScreeningPolicy()
    private val caller = CallerIdentity.Number("9876543210")

    @Test
    fun `unavailable policy defaults to allow`() {
        val result = policy.evaluate(caller, rules = null)
        assertEquals(ScreeningAction.ALLOW, result.action)
        assertNull(result.matchedRuleId)
    }

    @Test
    fun `no matching rule defaults to allow`() {
        val result = policy.evaluate(
            caller,
            listOf(rule("other", CallerMatcher.ExactNumber("+911111111111"), ScreeningAction.REJECT))
        )
        assertEquals(ScreeningAction.ALLOW, result.action)
    }

    @Test
    fun `higher priority wins before matcher specificity`() {
        val result = policy.evaluate(
            caller,
            listOf(
                rule("exact", CallerMatcher.ExactNumber(caller.normalized), ScreeningAction.REJECT, priority = 1),
                rule("prefix-high", CallerMatcher.NumberPrefix("987654"), ScreeningAction.SILENCE, priority = 2)
            )
        )
        assertEquals("prefix-high", result.matchedRuleId)
        assertEquals(ScreeningAction.SILENCE, result.action)
    }

    @Test
    fun `more specific matcher wins when priority is equal`() {
        val result = policy.evaluate(
            caller,
            listOf(
                rule("prefix", CallerMatcher.NumberPrefix("+91"), ScreeningAction.SILENCE),
                rule("exact", CallerMatcher.ExactNumber(caller.normalized), ScreeningAction.REJECT)
            )
        )
        assertEquals("exact", result.matchedRuleId)
        assertEquals(ScreeningAction.REJECT, result.action)
    }

    @Test
    fun `earlier creation order breaks remaining ties`() {
        val result = policy.evaluate(
            caller,
            listOf(
                rule("later", CallerMatcher.NumberPrefix("+91"), ScreeningAction.REJECT, creationOrder = 20),
                rule("earlier", CallerMatcher.NumberPrefix("+91"), ScreeningAction.SILENCE, creationOrder = 10)
            )
        )
        assertEquals("earlier", result.matchedRuleId)
    }

    @Test
    fun `disabled rules are ignored`() {
        val result = policy.evaluate(
            caller,
            listOf(
                rule("disabled", CallerMatcher.ExactNumber(caller.normalized), ScreeningAction.REJECT, enabled = false)
            )
        )
        assertEquals(ScreeningAction.ALLOW, result.action)
    }

    @Test
    fun `withheld and unknown caller IDs use their dedicated matcher`() {
        val hiddenRule = rule("hidden", CallerMatcher.WithheldOrUnknown, ScreeningAction.SILENCE)
        assertEquals("hidden", policy.evaluate(CallerIdentity.Withheld, listOf(hiddenRule)).matchedRuleId)
        assertEquals("hidden", policy.evaluate(CallerIdentity.Unknown, listOf(hiddenRule)).matchedRuleId)
        assertEquals(ScreeningAction.ALLOW, policy.evaluate(CallerIdentity.Missing, listOf(hiddenRule)).action)
    }

    @Test
    fun `saved contacts are allowed by default`() {
        val result = policy.evaluate(
            caller,
            rules = emptyList(),
            isSavedContact = true
        )
        assertEquals(ScreeningAction.ALLOW, result.action)
        assertNull(result.matchedRuleId)
    }

    private fun rule(
        id: String,
        matcher: CallerMatcher,
        action: ScreeningAction,
        priority: Int = 0,
        creationOrder: Long = 0,
        enabled: Boolean = true
    ) = ScreeningRule(id, priority, creationOrder, enabled, matcher, action)
}
