# Repository Guidelines

Guidance for AI coding agents (Copilot, Cursor, Aider, Claude, etc.) working in Stream’s Android Feeds repo.

### Repository purpose
Delivers the Stream Feeds Android SDK, network layer, and sample app. Guard API stability, network resilience, and low-regression releases.

### Tech & toolchain
- Kotlin targeting JVM 11; Gradle Kotlin DSL with shared logic in `buildSrc/`
- Retrofit, OkHttp, Moshi (KSP) for networking and serialization
- Spotless (ktfmt), Detekt, explicit API, Kover for static analysis and coverage
- Tests rely on JUnit4, MockK, Turbine, coroutines-test, MockWebServer

## Project structure
- `stream-feeds-android-client/` – public SDK sources and tests
- `stream-feeds-android-network/` – shared HTTP + serialization layer
- `stream-feeds-android-sample/` – demo app for manual verification
- `buildSrc/`, `config/`, `metrics/` – Gradle helpers, lint configs, coverage

## Build, test, and validation
- `./gradlew assemble` builds release artifacts; `./gradlew check` runs tests + static analysis
- `./gradlew :stream-feeds-android-client:test` is the fast loop; `./gradlew :stream-feeds-android-sample:installDebug` deploys the demo
- Run `./gradlew spotlessApply` then `./gradlew detekt` before committing

## Coding principles
- Keep coroutines structured and thread safe around token refresh, pagination, realtime updates
- Reuse shared retry/backoff helpers instead of bespoke networking logic
- Log via `StreamLogger` with actionable context and no secrets

## Style & conventions
- Spotless enforces Kotlin style (4 spaces, ktfmt, no wildcard imports)
- Public APIs need KDoc + explicit visibility; coordinate breaking changes
- PascalCase types, camelCase members, expressive builder verbs; backticked test names when helpful

## Testing guidance
- Place tests under each module’s `src/test/kotlin`, mirroring package names
- Cover success, retry, and failure paths for feed mutations, token refresh, and connectivity loss
- Use MockWebServer/fakes plus Turbine + coroutines-test for deterministic assertions

## Security & configuration
- Never hardcode API keys or tokens; rely on `local.properties`/env vars in the sample app
- Scrub sensitive data from logs and configs; coordinate edits in `scripts/` and `gradle/libs.versions.toml`

## PR & release hygiene
- Use imperative commit subjects, optional scope or `[skip ci]`, and `(#123)` for linked work
- Squash related commits, attach command output/screenshots for sample changes, and run `./gradlew check` pre-PR
- Flag breaking API or publishing-impacting work early and update docs when behaviour shifts

### Quick checklist for agents
- [ ] Run module-level tests (`:module:test`)
- [ ] Apply Spotless + Detekt and fix findings
- [ ] Document and cover new or changed APIs
