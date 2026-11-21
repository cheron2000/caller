package com.callvanta.ai.telecom.screening

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService
import com.callvanta.ai.CallvantaApplication
import com.callvanta.ai.domain.history.ScreeningEvent
import com.callvanta.ai.domain.screening.CallerIdentity
import com.callvanta.ai.domain.screening.ScreeningAction
import com.callvanta.ai.domain.screening.ScreeningPolicy
import java.util.UUID

/**
 * Local screening adapter. The Telecom response is sent before event persistence
 * is queued, keeping storage work out of the time-critical screening path.
 */
class CallvantaCallScreeningService : CallScreeningService() {
    private val policy = ScreeningPolicy()

    override fun onScreenCall(callDetails: Call.Details) {
        // Android documents that respondToCall has no effect for outgoing calls.
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) return

        var event: ScreeningEvent? = null
        val response = try {
            val handle: Uri? = callDetails.handle
            val caller = CallerIdentityMapper.fromHandle(
                scheme = handle?.scheme,
                schemeSpecificPart = handle?.schemeSpecificPart
            )
            val decision = policy.evaluate(caller = caller, rules = CallvantaApplication.rulesSnapshot())
            event = ScreeningEvent(
                id = UUID.randomUUID().toString(),
                occurredAtEpochMillis = System.currentTimeMillis(),
                callerNumber = (caller as? CallerIdentity.Number)?.normalized,
                action = decision.action
            )
            responseFor(decision.action)
        } catch (_: Exception) {
            event = ScreeningEvent(
                id = UUID.randomUUID().toString(),
                occurredAtEpochMillis = System.currentTimeMillis(),
                callerNumber = null,
                action = ScreeningAction.ALLOW
            )
            allowResponse()
        }

        respondToCall(callDetails, response)
        event?.let { (application as CallvantaApplication).recordScreeningEvent(it) }
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
