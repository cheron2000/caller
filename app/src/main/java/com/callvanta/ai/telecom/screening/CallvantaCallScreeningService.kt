package com.callvanta.ai.telecom.screening

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import com.callvanta.ai.CallvantaApplication
import com.callvanta.ai.domain.screening.ScreeningAction
import com.callvanta.ai.domain.screening.ScreeningPolicy

/**
 * Minimal Android adapter for the local screening policy.
 *
 * The first milestone deliberately has no persisted rules yet, so this adapter
 * evaluates an empty in-memory rule set and allows calls by default. Keep this
 * callback synchronous, bounded, and free of I/O to satisfy Telecom's deadline.
 */
class CallvantaCallScreeningService : CallScreeningService() {
    private val policy = ScreeningPolicy()

    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return

        val response = try {
            val handle: Uri? = callDetails.handle
            val caller = CallerIdentityMapper.fromHandle(
                scheme = handle?.scheme,
                schemeSpecificPart = handle?.schemeSpecificPart
            )
            val decision = policy.evaluate(caller = caller, rules = CallvantaApplication.rulesSnapshot())
            responseFor(decision.action)
        } catch (_: Exception) {
            allowResponse()
        }

        respondToCall(callDetails, response)
    }

    private fun responseFor(action: ScreeningAction): CallResponse =
        when (action) {
            ScreeningAction.ALLOW -> allowResponse()
            ScreeningAction.SILENCE -> CallResponse.Builder()
                .setSilenceCall(true)
                .build()
            ScreeningAction.REJECT -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .build()
        }

    private fun allowResponse(): CallResponse = CallResponse.Builder().build()
}
