package com.callvanta.ai.telecom.screening

import com.callvanta.ai.domain.screening.CallerIdentity
import com.callvanta.ai.domain.screening.NumberNormalizer

/**
 * Converts only the public telephone handle supplied by Telecom.
 * Android does not deliver withheld/private presentations to this service.
 */
internal object CallerIdentityMapper {
    fun fromHandle(scheme: String?, schemeSpecificPart: String?): CallerIdentity {
        if (scheme != "tel") return CallerIdentity.Missing
        val number = schemeSpecificPart?.let(NumberNormalizer::normalizeIncoming)
            ?: return CallerIdentity.Missing
        return CallerIdentity.Number(number)
    }
}
