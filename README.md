# S2B Hayagriva — Full Execution Foundation

This repository is a clean Android project implementing the usable, permission-safe foundation of the S2B Hayagriva plan.

## Build
Use JDK 17 and Gradle 8.9:
`gradle :app:assembleDebug`

GitHub Actions is included at `.github/workflows/main.yml` and builds the APK.

## Included working modules
- Always-dark Hayagriva UI
- Animated central Hayagriva orb placeholder
- Tap-to-talk speech recognition
- Text-to-speech responses
- English/Hindi/Tamil language selection
- Local profile/authentication demo
- Camera launch
- GPS/location permission and coordinates
- Weather via Open-Meteo
- Battery and RAM information
- Alarm intent
- Calendar intent
- File picker
- PDF generation
- Share/download-style Android intents
- Translation mode foundation
- Math calculator foundation
- Coding/persona/research modes
- Conversation history in memory for the current session
- Secure Gemini backend hook (API key is NOT embedded in APK)

## Important
Some capabilities require OS-level privileges, paired devices, or external credentials and are intentionally implemented as safe integration points:
- Gemini Live / Gemini API
- True always-on wake word
- SMS/call automation
- Accessibility/screen reading
- Smart TV/watch/family hub
- Deep research providers
- Shopping providers
- Desktop/USB companion

See `docs/IMPLEMENTATION_STATUS.md`.

## Gemini
Use the included backend:
`backend/`
Set `GEMINI_API_KEY` in the backend environment. Never put the key in the Android source.
