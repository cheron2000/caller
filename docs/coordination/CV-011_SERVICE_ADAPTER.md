# CV-011 — Android CallScreeningService Adapter

## Implemented boundary

The app manifest registers `CallvantaCallScreeningService` with the protected `BIND_SCREENING_SERVICE` permission and Telecom service action. Android activates it only when Callvanta holds the call-screening role.

The service:
1. Ignores outgoing callbacks.
2. Reads only the provided call handle.
3. Maps a telephone handle to a caller number; absent or non-telephone metadata maps to `CallerIdentity.Missing`.
4. Evaluates the pure Kotlin policy synchronously with an empty in-memory rule list.
5. Responds with allow, silence, or reject using Android's `CallResponse.Builder`.
6. Falls back to allow if local mapping/evaluation throws.

No storage, network, AI, audio capture, UI launch, or additional permissions are used in the callback. Since persistent/user-configurable rules are not implemented, the current adapter allows every incoming call.

## Platform constraints confirmed

Android's API reference states that the callback may include incoming and outgoing calls, and incoming calls must be answered within five seconds. The provided `Call.Details` exposes a limited set of fields, including direction and handle. Only telephone-scheme handles are delivered. Without `READ_CONTACTS`, calls from numbers in the user's contacts are not delivered. Restricted, unknown, unavailable, and payphone presentations are also not delivered to third-party screening services.

Consequently, saved-contact and withheld-caller rules remain domain-level future capabilities, not capabilities exercised by this adapter. Do not add contacts permission or claim those calls are screened without a separate owner-approved scope change.

## Verification gate

CI must compile the Android service and run JVM tests. This is not a substitute for manual validation on the target Vivo Y39. Confirm exact device build/API, role grant, incoming SIM callback, allow behavior, and response timing on-device before making compatibility claims.

## Sources

- Android Developers — CallScreeningService API reference: https://developer.android.com/reference/android/telecom/CallScreeningService
- Android Developers — Screen calls guide: https://developer.android.com/develop/connectivity/telecom/dialer-app/screen-calls
