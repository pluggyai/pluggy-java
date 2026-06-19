# Sync SDK with API

Synchronize the Pluggy Java SDK with the current production API.

## Steps

### 1. Fetch Current API Specification

Fetch the OpenAPI 3.1 spec. The endpoint requires authentication via `x-api-key`:

```bash
doppler run -- bash -c '
  apikey=$(curl -s -X POST https://api.pluggy.ai/auth \
    -H "Content-Type: application/json" \
    -d "{\"clientId\":\"$PLUGGY_CLIENT_ID\",\"clientSecret\":\"$PLUGGY_CLIENT_SECRET\"}" \
    | jq -r .apiKey)
  curl -s https://api.pluggy.ai/oas3.json -H "x-api-key: $apikey" -o /tmp/pluggy-oas.json
'
```

If Doppler is not configured, set `PLUGGY_CLIENT_ID` and `PLUGGY_CLIENT_SECRET` manually.

### 2. Compare Endpoints

Review the Retrofit interface `src/main/java/ai/pluggy/client/PluggyApiService.java` against the OpenAPI `paths`.

**Check for:**
- Missing endpoints (paths in OAS but not in `PluggyApiService`)
- Missing HTTP methods on existing paths
- Path parameters declared with `@Path` matching OAS `{param}` placeholders
- Query parameters: single `@Query("name")` vs. `@QueryMap SearchRequest` patterns
- Request body shape (`@Body` DTO) matches the OAS request schema
- Response type (`Call<ResponseType>`) matches the OAS response schema

### 3. Compare Types

Review types in `src/main/java/ai/pluggy/client/response/` and `src/main/java/ai/pluggy/client/request/` against the OAS `components.schemas`.

**Check for:**
- Missing fields in existing DTOs
- New schemas not yet defined as DTOs
- **Field name casing**: the Gson instance uses `FieldNamingPolicy.IDENTITY`, so Java field names must match OAS property names **byte-for-byte** (e.g. OAS `payeeMCC` → Java `payeeMCC`, not `payeeMcc`). A single-character mismatch silently deserializes to `null`. This is the single highest-impact gap class — audit casing carefully. See commit `d1befb0` for prior incident.
- Type mismatches (e.g. OAS `integer` vs Java `Integer`, OAS `array` vs `List<T>` / `T[]`)
- Nullability: Java has no `?` syntax; use boxed types (`Integer`, `Double`, `Boolean`) for nullable fields. Primitives (`int`, `double`, `boolean`) cannot represent null.

### 4. Document Findings

Produce a gap analysis grouped by:
- **Missing endpoints** — with priority and HTTP method
- **Missing fields** — by DTO, with API name, Java name, type, nullability
- **Casing mismatches** — highest priority because they fail silently
- **Type mismatches** — flag and propose fix
- **Breaking changes** (if the OAS removed a field the SDK still exposes)

### 5. Implementation Order

1. **Phase 1 — Casing fixes**: rename Java fields to match OAS exactly. Highest impact because failures are silent.
2. **Phase 2 — Missing fields**: add new fields to existing DTOs (see `add-model-fields`).
3. **Phase 3 — Missing endpoints**: implement new endpoints (see `add-endpoint`).
4. **Phase 4 — Type corrections**: fix any wrong types.

### 6. Validation

After implementation:
```bash
mvn -B package          # compile + unit tests
doppler run -- mvn -B verify   # integration tests against live API
```

### 7. Update CLAUDE.md

If significant gaps were closed, add a line under "Architecture" noting the sync date and what was added.
