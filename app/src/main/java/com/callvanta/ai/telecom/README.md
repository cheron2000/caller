# Telecom integration

## Call screening adapter

`CallvantaCallScreeningService` is registered as the Android `CallScreeningService` implementation and is activated only when the user grants the call-screening role.

- Evaluates incoming calls synchronously against the immutable rule snapshot loaded by the Application.
- Normalizes incoming telephone handles before policy matching.
- Maps allow, silence, and reject to the corresponding `CallResponse` fields.
- Fails open if caller metadata or policy evaluation is unavailable.
- Performs no database, network, AI, audio, or UI work in the callback.
- Does not request `READ_CONTACTS` or `READ_CALL_LOG`.
- Returns without responding to outgoing callbacks because Android documents that `respondToCall()` has no effect for outgoing calls.

## Android platform limits

Android sends only calls whose handle uses the `tel` scheme and, without `READ_CONTACTS`, only calls not in the user's contacts. Restricted, unknown, unavailable, and payphone presentations are not delivered to third-party screening services. Therefore, this adapter cannot currently apply rules to saved contacts or withheld caller IDs. The UI does not offer new rules for these unsupported categories and labels any legacy withheld/unknown rules as unsupported.

## Number normalization

Indian numbers are normalized to their 10-digit national significant number. For example, `+91 98765 43210`, `09876543210`, and `98765-43210` normalize to `9876543210`. Explicit international E.164 numbers remain in +country-code form. Prefix rules require at least six significant digits; a country code alone is rejected as too broad.

The framework requires a response to incoming calls within five seconds. Keep this path synchronous, deterministic, and free of I/O. No device-specific compatibility claim is made until tested on the target Vivo Y39.
