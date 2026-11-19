# Callvanta — Architecture

## 1. Architectural principles

- Android platform APIs are the authority for call state and supported telecom operations.
- Keep call-screening decisions independent of remote AI services.
- Separate presentation, telecom adapters, domain logic, persistence, and network clients.
- Treat AI-generated content as derived data and label it accordingly.
- Make privacy and consent requirements part of feature design.
- Use a provider-neutral AI boundary so the model provider can change without redesigning the Android app.

## 2. High-level component view

```mermaid
flowchart TD
    USER[Device owner] --> UI[Android UI]
    UI --> DOMAIN[Application / domain layer]
    TELECOM[Android Telecom APIs] --> ADAPTER[Telecom adapters]
    ADAPTER --> DOMAIN
    DOMAIN --> DATA[Local persistence]
    DOMAIN --> AI_CLIENT[AI service client]
    AI_CLIENT --> API[Optional FastAPI backend]
    API --> LLM[LLM provider]
    API --> SPEECH[Optional speech services]
```

The diagram is conceptual. It does not imply that Android exposes unrestricted cellular-call audio to the app.

## 3. Android client layers

### Presentation layer — `app/.../ui/`

Responsibilities:
- Dashboard and setup flow.
- Call history and call detail screens.
- Screening preferences and privacy settings.
- Permission and default-dialer role explanations.
- Loading, unavailable, and error states.

Suggested feature areas:
- `dashboard/`
- `calls/`
- `settings/`
- `onboarding/`

### Telecom integration — `app/.../telecom/`

Responsibilities:
- Adapt supported Android Telecom callbacks into app-level events.
- Implement call screening as a bounded, deterministic operation.
- Later evaluate default-dialer integration through `InCallService`.
- Report capability availability and failures to the domain layer.

Key platform boundaries:
- `CallScreeningService`: screening decisions, not an AI call conversation.
- `InCallService`: default-dialer in-call UI/control integration, subject to role and API contracts.
- Cellular call audio: restricted; do not assume direct access to uplink/downlink streams.

### Domain layer — `app/.../domain/`

Responsibilities:
- Call and caller models.
- Screening policy and decision rules.
- Use cases for call history and settings.
- Interfaces for repositories and optional AI operations.
- Capability checks and feature gating.

### Data layer — `app/.../data/`

Responsibilities:
- Local persistence using Room when implementation begins.
- Repository implementations.
- Data mapping and retention/deletion behavior.
- Secure handling of user-controlled configuration.

### AI and speech boundary — `app/.../ai/`

Responsibilities:
- Define provider-independent request/response contracts.
- Coordinate permitted speech recognition and speech synthesis.
- Request caller-purpose extraction or summarization only from data the app is allowed to process.
- Handle timeouts, offline state, and service failures.

Do not put provider secrets in this package or in the APK.

## 4. Optional backend

Proposed backend: Python + FastAPI.

Responsibilities:
- Authenticate app requests.
- Validate and minimize submitted data.
- Call the configured LLM provider using server-side secrets.
- Return structured, bounded outputs such as summaries or caller-purpose labels.
- Apply rate limits, request limits, and privacy-aware logging.

The backend is optional for core app operation. A backend outage must not prevent basic call UI, local settings, or local call records from functioning.

## 5. Data flow

### Screening flow

1. Android invokes the screening service for an incoming call.
2. The service reads only available call metadata.
3. A local policy evaluates the configured rule.
4. The service returns a supported screening response within the platform deadline.
5. The app stores the result and updates the UI when permitted.

No remote LLM call should be required to meet the screening deadline.

### Post-call summary flow (future, conditional)

1. Obtain permitted text or other supported input.
2. Clearly disclose any remote processing.
3. Send the minimum necessary data to the backend.
4. Backend validates and requests a structured summary from the model provider.
5. App labels the result as AI-generated and stores it only according to user settings.

This flow does not presume that cellular call audio can be captured.

## 6. Proposed data entities

- `CallRecord`: call identifier, number/contact reference when available, timestamps, direction, duration when available, screening outcome.
- `CallerProfile`: user-managed caller label and notes.
- `ScreeningRule`: enabled state, matching criteria, selected action.
- `CallInsight`: AI-generated purpose or summary, source type, created timestamp.
- `AppPreferences`: privacy, retention, and feature settings.

Store only necessary fields. Avoid retaining raw audio by default.

## 7. Security boundaries

- Android permissions and role grants are user-controlled.
- Backend credentials are held in deployment secrets/environment variables.
- App-to-backend requests use authenticated transport.
- Do not commit API keys, tokens, personal call data, or real transcripts.
- Logs must avoid sensitive caller content.
- Provide deletion and retention controls.

## 8. Quality attributes

- Modular boundaries allow telecom implementation to be tested separately from UI.
- Screening path is local and predictable.
- AI calls are asynchronous and never block telecom callbacks.
- Capability detection gates unsupported features.
- Interfaces permit mock implementations for tests when implementation begins.
