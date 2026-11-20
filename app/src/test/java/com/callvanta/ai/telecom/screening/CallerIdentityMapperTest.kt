package com.callvanta.ai.telecom.screening

import com.callvanta.ai.domain.screening.CallerIdentity
import org.junit.Assert.assertEquals
import org.junit.Test

class CallerIdentityMapperTest {
    @Test
    fun `tel handle maps to normalized number`() {
        assertEquals(
            CallerIdentity.Number("+919876543210"),
            CallerIdentityMapper.fromHandle("tel", "+919876543210")
        )
    }

    @Test
    fun `non tel handle fails open as missing identity`() {
        assertEquals(CallerIdentity.Missing, CallerIdentityMapper.fromHandle("sip", "user@example.com"))
    }

    @Test
    fun `missing handle fails open as missing identity`() {
        assertEquals(CallerIdentity.Missing, CallerIdentityMapper.fromHandle(null, null))
    }

    @Test
    fun `blank tel number fails open as missing identity`() {
        assertEquals(CallerIdentity.Missing, CallerIdentityMapper.fromHandle("tel", "  "))
    }
}
