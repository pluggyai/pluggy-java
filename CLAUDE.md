# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java SDK ("bindings") for the Pluggy API (https://docs.pluggy.ai). Published as `ai.pluggy:pluggy-java` to GitHub Packages. Built on Retrofit 2 + OkHttp 3 + Gson; targets Java 1.8.

## Common commands

```bash
# Compile + unit tests (Surefire; excludes integration tests under **/integration/**)
mvn -B package

# Run unit tests only
mvn -B test

# Run a single unit test class / method
mvn -B test -Dtest=ClientParseErrorTest
mvn -B test -Dtest=ClientParseErrorTest#methodName

# Run integration tests (Failsafe; requires live Pluggy creds — see env vars below).
# Runs in parallel with 8 threads. Tests under src/test/java/ai/pluggy/client/integration/.
mvn -B verify

# Run a single integration test class
mvn -B verify -Dit.test=GetConnectorsTest -DfailIfNoTests=false
```

No lint/format plugin is configured (no checkstyle, spotless, PMD, errorprone) — formatting is enforced only by `.editorconfig`. Don't go looking for one.

Integration tests require the following env vars (loaded from Doppler in CI, see `.github/workflows/maven.yml`):

- `PLUGGY_CLIENT_ID`
- `PLUGGY_CLIENT_SECRET`
- `PLUGGY_BASE_URL`
- `PLUGGY_RSA_PUBLIC_KEY` (optional; only needed when exercising `EncryptedParametersInterceptor`)

Missing any of the first three causes `BaseApiIntegrationTest` to fail-fast in `@BeforeEach`. Each test that creates items appends the new id to `itemsIdCreated`; the base class's `@AfterEach` deletes them, so integration tests are responsible for tracking what they create.

## Architecture

The SDK is a thin Retrofit interface wrapped in a builder-configured client. Three things matter when reading or modifying it:

**1. `PluggyClient` (builder → Retrofit) — `src/main/java/ai/pluggy/client/PluggyClient.java`**

`PluggyClient.builder().clientIdAndSecret(...).baseUrl(...).build()` constructs an `OkHttpClient`, wires it into a Retrofit instance, and creates a `PluggyApiService` proxy. Default base URL is `https://api.pluggy.ai`; default timeouts are 10s connect / 180s read. The builder exposes `okHttpClientBuilder()` for advanced customization (SSL, custom interceptors, etc.) before `build()`. `noAuthInterceptor()` opts the caller out of the default auth interceptor — they must then add the `x-api-key` header themselves.

**2. `PluggyApiService` — the API surface**

Single Retrofit interface annotated with `@GET/@POST/@PATCH/@DELETE`. To add a new endpoint, add a method here; request/response DTOs live in `client/request/` and `client/response/`. The full list of supported endpoints is whatever is declared in this interface — there is no other routing layer.

**3. Two interceptors handle cross-cutting concerns**

- `ApiKeyAuthInterceptor` (`client/auth/`): transparently fetches the `x-api-key` JWT via `POST /auth` on first use, caches it in `TokenProvider`, decodes the JWT `exp` claim to detect expiry, and on a 401/403 with an expired key refreshes once and retries the original request. Also sets `User-Agent: PluggyJava/<version>` — note this string is hardcoded in two places (`PluggyClient.authenticate` and `ApiKeyAuthInterceptor.requestWithAuth`) and is not auto-derived from `pom.xml`.
- `EncryptedParametersInterceptor` (`client/auth/`): only attached when `rsaPublicKey(...)` is set on the builder. RSA/ECB/OAEPPadding-encrypts the `parameters` JSON field on POST/PATCH `/items` requests before they leave OkHttp.

### JSON / Gson conventions — important gotcha

The Gson instance uses `FieldNamingPolicy.IDENTITY`, **not** `LOWER_CASE_WITH_UNDERSCORES` or `CAMEL_CASE`. That means the Java field name in every DTO under `client/response/` and `client/request/` must match the API's JSON key **byte-for-byte** (e.g. API field `payeeMCC` → Java field `payeeMCC`, not `payeeMcc`). Commit `d1befb0` exists precisely because a casing mismatch silently produced a null. When adding fields, double-check casing against the live API response or `docs.pluggy.ai`.

Use `pluggyClient.parseError(response)` to deserialize a non-successful `retrofit2.Response` into an `ErrorResponse`. It gracefully falls back to a synthesized `ErrorResponse` if the body is not valid JSON.

For inbound webhook payloads, use `ai.pluggy.utils.Utils.parseWebhookEventPayload(json)` — it returns a typed `WebhookEventPayload` with the discriminated `event` enum (`item/created`, `connector/status_updated`, etc.). See `WebhookEventParseTest` for example payload shapes.

### Test layout

- `src/test/java/ai/pluggy/client/unit/` — fast, no network. Run by Surefire during `mvn package` / `mvn test`.
- `src/test/java/ai/pluggy/client/integration/` — hit the live API. Excluded from Surefire (`pom.xml` config) and run only by Failsafe during `mvn verify`. All extend `BaseApiIntegrationTest`. Helpers in `integration/util/` (e.g. `AssertionsUtils.assertSuccessful`) and `integration/helper/` (factories for items/tokens).

## Release flow

Version lives in `pom.xml`. `release.yml` runs on every push to `master`: it reads `project.version` from the pom, creates the tag `v<version>` if it doesn't already exist, and publishes a GitHub Release. The GitHub Release in turn triggers `maven-publish.yml`, which deploys the jar to GitHub Packages (`maven.pkg.github.com/pluggyai/pluggy-java`). Consequence: bumping the version in `pom.xml` and merging to master ships a release — **do not bump the version unless you intend to cut one**.

## Style

`.editorconfig` enforces 2-space indent, LF line endings, UTF-8, final newline. Code uses Lombok (`@SneakyThrows`, getters/setters/builders) — keep the Lombok dependency at `provided` scope.
