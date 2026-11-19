# Proposed Repository Structure

This is a planned structure, not an implemented Android project. Directories are represented by documentation placeholders until the implementation phase begins.

```text
caller/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/nova/ai/
│           │   ├── ui/
│           │   │   ├── dashboard/
│           │   │   ├── calls/
│           │   │   ├── settings/
│           │   │   └── onboarding/
│           │   ├── telecom/
│           │   ├── domain/
│           │   ├── data/
│           │   └── ai/
│           └── res/
├── backend/
│   ├── api/
│   ├── services/
│   ├── schemas/
│   └── tests/
├── docs/
├── .github/
│   └── workflows/
└── README.md
```

## Directory responsibilities

| Directory | Responsibility |
|---|---|
| `app/.../ui` | Compose screens, navigation, and presentation state |
| `app/.../telecom` | Android Telecom service adapters and platform integration |
| `app/.../domain` | Call models, policies, use cases, and repository contracts |
| `app/.../data` | Room database, data sources, and repository implementations |
| `app/.../ai` | Provider-neutral AI and permitted speech integration boundaries |
| `backend/api` | FastAPI route definitions |
| `backend/services` | AI provider orchestration and business services |
| `backend/schemas` | Request/response contracts |
| `backend/tests` | Backend test suite |
| `docs` | Product, architecture, platform, and operational documentation |
| `.github/workflows` | Future CI and APK build workflows |

## Structure rules

- Keep UI code separate from business rules.
- Keep Android platform APIs behind telecom adapters.
- Keep network and persistence details out of UI components.
- Keep provider-specific logic behind a stable AI interface.
- Never store API keys or user call data in source control.
- Add build/configuration files only when implementation starts; this scaffold intentionally does not compile.
