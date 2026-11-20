package com.callvanta.ai.telecom.screening

import com.callvanta.ai.domain.screening.CallerIdentity

/**
 * Converts only the public handle fields supplied by Telecom.
 *
 * Restricted/withheld presentations are not delivered to third-party screening
 * services by Android. A missing or non-telephone handle is therefore treated
 * as missing metadata, never inferred to be a withheld caller.
 */
internal object CallerIdentityMapper {
    fun fromHandle(scheme: String?, schemeSpecificPart: String?): CallerIdentity {
        if (scheme != "tel") return CallerIdentity.Missing
        val number = schemeSpecificPart?.trim().orEmpty()
        return if (number.isBlank()) CallerIdentity.Missing else CallerIdentity.Number(number)
    }
}
