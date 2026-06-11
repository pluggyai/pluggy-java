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

The version in `pom.xml` (`<version>` on line 7) is the **single source of truth** — there is no automatic version bumping. Releases are fully automated off the version in the pom:

```
bump <version> in pom.xml  ──►  merge to master
        │
        ▼
release.yml  (on: push to master; also workflow_dispatch w/ `force`)
  reads project.version → TAG = v<version>
  if TAG already exists (and not forced): NO-OP, nothing is published
  else: creates annotated tag v<version>, generates notes from
        git log <prev-tag>..HEAD, publishes a GitHub Release
        │  (GitHub Release "created" event)
        ▼
maven-publish.yml  (on: release created; also workflow_dispatch w/ `tag_version`)
  build  : mvn -B package
  test   : mvn -B verify   (integration tests, creds from Doppler)
  deploy : mvn deploy -Dmaven.test.skip.exec -s settings.xml
           → GitHub Packages (maven.pkg.github.com/pluggyai/pluggy-java)
           (deploy job needs `packages: write`)
```

### How to cut a release

1. Bump `<version>` in `pom.xml` (semver: `feat:` commits since the last tag → minor, `fix:`/`chore:` only → patch).
2. Open a PR with the bump. PR title must be a conventional commit (enforced by `pr-title.yml`), e.g. `chore(release): bump version to 1.10.0`.
3. Merge to `master`. The merge triggers `release.yml`, which tags `v<version>`, cuts the GitHub Release, and that in turn triggers `maven-publish.yml` to build → test → deploy.
4. Verify: `gh run list --workflow=release.yml`, `gh release view v<version>`, and that the deploy job in `maven-publish.yml` succeeded.

**Gotchas:**
- **Forgetting the bump is a silent no-op.** If the pom version already has a tag, the push to master publishes nothing. Don't bump the version unless you intend to cut a release, and *do* bump it when you do.
- The `maven-release-plugin` is configured in `pom.xml` (`tagNameFormat = v@{project.version}`) but is **not** used by CI — tagging is done by `release.yml`'s shell, not `mvn release:*`.
- Manual escape hatches: run `release.yml` via `workflow_dispatch` with `force=true` to re-release an existing tag; run `maven-publish.yml` via `workflow_dispatch` with a `tag_version` input to re-deploy an existing tag without re-tagging.

## Style

`.editorconfig` enforces 2-space indent, LF line endings, UTF-8, final newline. Code uses Lombok (`@SneakyThrows`, getters/setters/builders) — keep the Lombok dependency at `provided` scope.
