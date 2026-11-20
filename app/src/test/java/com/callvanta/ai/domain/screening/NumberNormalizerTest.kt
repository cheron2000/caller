package com.callvanta.ai.domain.screening

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NumberNormalizerTest {
    @Test
    fun `normalizes common Indian exact number formats`() {
        assertEquals("9876543210", NumberNormalizer.normalizeExact("+91 98765 43210"))
        assertEquals("9876543210", NumberNormalizer.normalizeExact("09876543210"))
        assertEquals("9876543210", NumberNormalizer.normalizeExact("98765-43210"))
        assertEquals("9876543210", NumberNormalizer.normalizeExact("9876543210"))
    }

    @Test
    fun `normalizes incoming Indian number formats identically`() {
        assertEquals("9876543210", NumberNormalizer.normalizeIncoming("+919876543210"))
        assertEquals("9876543210", NumberNormalizer.normalizeIncoming("9876543210"))
    }

    @Test
    fun `preserves explicit international E164 numbers`() {
        assertEquals("+12025550123", NumberNormalizer.normalizeExact("+1 202-555-0123"))
    }

    @Test
    fun `rejects invalid exact numbers`() {
        assertNull(NumberNormalizer.normalizeExact("123"))
        assertNull(NumberNormalizer.normalizeExact("+91"))
        assertNull(NumberNormalizer.normalizeExact("abc9876543210"))
    }

    @Test
    fun `normalizes specific Indian prefixes`() {
        assertEquals("987654", NumberNormalizer.normalizePrefix("987-654"))
        assertEquals("987654", NumberNormalizer.normalizePrefix("+91 987654"))
        assertEquals("987654", NumberNormalizer.normalizePrefix("0987654"))
    }

    @Test
    fun `rejects overly broad prefixes`() {
        assertNull(NumberNormalizer.normalizePrefix("+91"))
        assertNull(NumberNormalizer.normalizePrefix("9"))
        assertNull(NumberNormalizer.normalizePrefix("98765"))
    }
}
