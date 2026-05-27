# Add Model Fields

Add missing fields to existing DTOs in the Pluggy Java SDK.

## Required Information

- **DTO name**: e.g. `Transaction`, `Investment`, `Account`
- **Field name (OAS)**: exact JSON property name from the OpenAPI spec — **case-sensitive**
- **Field type**: Java type (`String`, `Integer`, `Double`, `Boolean`, `Date`, custom DTO, array/list)
- **Nullability**: nullable by default in this SDK. Use boxed types (`Integer`, `Double`) for nullable numerics; primitives only when guaranteed non-null.
- **Description**: brief comment if the field is non-obvious (avoid otherwise)

## Critical: Gson IDENTITY casing

The Gson instance is configured with `FieldNamingPolicy.IDENTITY`. Java field names must match the OAS JSON property name **byte-for-byte**. `payeeMCC` ≠ `payeeMcc` — a casing mismatch silently produces `null` with no warning. **Always copy the OAS field name exactly, including unusual casing.**

## Steps

### 1. Locate the DTO

DTOs live in `src/main/java/ai/pluggy/client/response/` (response payloads) and `src/main/java/ai/pluggy/client/request/` (request bodies and query maps).

### 2. Verify the OAS field name

Fetch the OAS (see `sync-api`) and inspect the schema:

```bash
jq '.components.schemas.<SchemaName>.properties' /tmp/pluggy-oas.json
```

Copy the property key **exactly** as it appears.

### 3. Add the field

Response DTO pattern (Lombok `@Data @Builder`, package-private fields):

```java
@Data
@Builder
public class Transaction {
  String id;
  String accountId;
  Double amount;
  // ... existing fields ...

  // New field — name must match OAS exactly
  String operationType;
}
```

Request DTO pattern (Lombok `@Value @AllArgsConstructor @Builder` with convenience constructors):

```java
@Value
@AllArgsConstructor
@Builder
public class CreateItemRequest {
  Integer connectorId;
  ParametersMap parameters;
  // ... existing fields ...

  // New optional field
  String newField;

  // If you add a field, update each convenience constructor to set it to null
  public CreateItemRequest(Integer connectorId, ParametersMap parameters) {
    this.connectorId = connectorId;
    this.parameters = parameters;
    // ... null-out the rest, including newField ...
    this.newField = null;
  }
}
```

### 4. Type patterns

```java
// Nullable string
String name;

// Non-null primitive (only if API guarantees it)
int count;

// Nullable number (default — boxed)
Integer count;
Double balance;

// Boolean
Boolean isActive;

// Date — usually serialized as ISO 8601 string in the API; prefer String
// unless an existing pattern in a sibling DTO uses java.util.Date
String createdAt;

// Nested DTO
CreditData creditData;

// Array
String[] tags;
SubModel[] items;

// Enum — declared as a separate file under response/ or request/
ProductType[] products;
```

### 5. Add nested DTOs if needed

If the field is a complex type new to the SDK, create a sibling file in the same package and follow the `@Data @Builder` pattern (for response) or `@Value @Builder` (for request).

### 6. Build and test

```bash
mvn -B package          # compile + unit tests
doppler run -- mvn -B verify   # integration tests
```

For deserialization changes, integration tests are the real signal — they exercise real API responses.

### 7. Verification checklist

- [ ] Field name matches OAS byte-for-byte (no case smoothing)
- [ ] Nullable fields use boxed types
- [ ] Existing convenience constructors updated (for request DTOs)
- [ ] `mvn -B verify` passes
