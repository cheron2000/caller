# Callvanta — Android Call Assistant

Callvanta is an Android application for managing incoming SIM calls, with a planned cloud AI receptionist that can answer calls forwarded by the carrier when the user is unavailable.

## Current implementation

- Android app built with Kotlin and Jetpack Compose.
- Incoming-call screening through Android's supported CallScreeningService.
- Local exact-number and prefix rules, including saved-contact rules after the user grants Contacts permission.
- Local history of Callvanta-observed screening events, with deletion and 30-day retention.
- Android CI workflow builds a debug APK and runs unit tests.
- Python/FastAPI voice-gateway scaffold for a future Exotel Voicebot integration, with health/readiness endpoints, token-protected WebSocket handling, event parsing, PCM validation, tests, and separate backend CI.

## AI receptionist status

The owner selected **conditional forwarding from the existing Airtel mobile number** (unanswered, busy, and unreachable where supported). The phone should ring normally first; unconditional forwarding is not the default.

The backend is currently only a protocol scaffold. It does **not** yet run speech recognition, an LLM conversation, text-to-speech, or outbound audio playback. Do not enable Airtel forwarding to it. Exotel Voicebot Applet is the first provider candidate, but account eligibility and feature enablement have not been confirmed.

The intended production flow is:

```text
Caller → Airtel network → user's phone rings
                        └─ unanswered/busy/unreachable → telephony provider
                                                        ↕ bidirectional audio
                                                  Callvanta voice backend
                                                  STT → conversation/LLM → TTS
```

The Android app remains the settings and call-history client; it does not capture ordinary cellular-call audio.

## Important platform and privacy boundaries

- `CallScreeningService` makes screening decisions; it is not a live answering interface.
- Android does not grant ordinary apps unrestricted access to both sides of cellular-call audio.
- Keep provider and AI credentials server-side, never in the APK or repository.
- The AI must identify itself and disclose AI processing before collecting caller information. Recording/transcript retention remains off unless separately approved with an appropriate disclosure and legal basis.
- No forwarding should be activated on the owner's personal SIM until the full voice pipeline works on a provider test number and the owner explicitly approves the test.

## Development

### Android
See [Android app instructions](app/README.md). GitHub Actions builds the debug APK.

### Voice backend
See [backend README](backend/README.md). Requires Python 3.11+:

```bash
cd backend
python -m venv .venv
pip install -e ".[dev]"
cp .env.example .env
uvicorn app.main:app --host 127.0.0.1 --port 8000
pytest
```

Set `CALLVANTA_EXOTEL_WS_TOKEN` locally or as a deployment secret. Never commit a real value.

## Documentation

- [Project plan](docs/PROJECT_PLAN.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Android platform constraints](docs/ANDROID_PLATFORM_CONSTRAINTS.md)
- [Roadmap](docs/ROADMAP.md)
- [Project decisions](docs/coordination/DECISIONS.md)
- [Backend implementation](backend/README.md)
