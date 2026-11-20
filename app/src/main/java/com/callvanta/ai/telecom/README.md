# Telecom integration

## Call screening adapter

`CallvantaCallScreeningService` is registered as the Android `CallScreeningService` implementation and is activated only when the user grants Callvanta the call-screening role.

- Responds only to incoming calls; outgoing callbacks are ignored.
- Evaluates the pure Kotlin policy synchronously with an empty in-memory rule set.
- Maps allow, silence, and reject to the corresponding `CallResponse` fields.
- Fails open if caller metadata or policy evaluation is unavailable.
- Performs no database, network, AI, audio, or UI work in the callback.
- Does not request `READ_CONTACTS` or `READ_CALL_LOG`.

## Android platform limits

Android sends only calls whose handle uses the `tel` scheme and, without `READ_CONTACTS`, only calls not in the user's contacts. Restricted, unknown, unavailable, and payphone presentations are not delivered to third-party screening services. Therefore, this adapter cannot currently apply rules to saved contacts or withheld caller IDs. The domain policy retains those categories for a future separately approved integration, but this adapter does not fabricate identities for calls Android does not provide.

The framework requires a response within five seconds. Keep this path synchronous, deterministic, and free of I/O. No device-specific compatibility claim is made until tested on the target Vivo Y39.
