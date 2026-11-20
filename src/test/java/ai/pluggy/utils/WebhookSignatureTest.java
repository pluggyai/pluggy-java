package ai.pluggy.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.utils.WebhookSignature.FailureReason;
import ai.pluggy.utils.WebhookSignature.Result;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WebhookSignatureTest {

  private static final String SECRET = "whsec_" + repeat("a1b2c3d4", 8);
  private static final String OTHER_SECRET = "whsec_" + repeat("f0e1d2c3", 8);
  private static final long T = 1763560800L;
  private static final Instant NOW = Instant.ofEpochSecond(T);
  private static final String BODY = "{\"event\":\"item/updated\",\"eventId\":\"evt_1\",\"itemId\":\"item_1\"}";
  private static final String V1 = sign(SECRET, T, BODY);

  // --- helpers ---

  private static String repeat(String s, int times) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < times; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  private static String sign(String secret, Object t, byte[] body) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      mac.update((t + ".").getBytes(StandardCharsets.UTF_8));
      byte[] digest = mac.doFinal(body);
      StringBuilder hex = new StringBuilder();
      for (byte b : digest) {
        hex.append(String.format("%02x", b));
      }
      return hex.toString();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  private static String sign(String secret, Object t, String body) {
    return sign(secret, t, body.getBytes(StandardCharsets.UTF_8));
  }

  private static String header(long t) {
    return "t=" + t + ",v1=" + sign(SECRET, t, BODY);
  }

  private static Result validate(String payload, String header, String... secrets) {
    return WebhookSignature.validate(payload, header, Arrays.asList(secrets),
      WebhookSignature.DEFAULT_TOLERANCE_SECONDS, NOW);
  }

  private static Result validateHeader(String header) {
    return validate(BODY, header, SECRET);
  }

  private static Result validateWithTolerance(String header, long toleranceSeconds) {
    return WebhookSignature.validate(BODY, header, Collections.singletonList(SECRET),
      toleranceSeconds, NOW);
  }

  private static void assertValid(Result result) {
    assertTrue(result.isValid(), "expected valid, got " + result);
    assertNull(result.getReason());
  }

  private static void assertInvalid(FailureReason expected, Result result) {
    assertFalse(result.isValid(), "expected " + expected + ", got valid");
    assertEquals(expected, result.getReason());
  }

  // --- constants ---

  @Test
  void exposesHeaderNameAndDefaultTolerance() {
    assertEquals("X-Pluggy-Signature", WebhookSignature.HEADER_NAME);
    assertEquals(300L, WebhookSignature.DEFAULT_TOLERANCE_SECONDS);
  }

  @Test
  void failureReasonCodesMatchTheOtherSdks() {
    assertEquals("no_secret", FailureReason.NO_SECRET.code());
    assertEquals("invalid_payload", FailureReason.INVALID_PAYLOAD.code());
    assertEquals("missing_header", FailureReason.MISSING_HEADER.code());
    assertEquals("malformed_header", FailureReason.MALFORMED_HEADER.code());
    assertEquals("timestamp_out_of_tolerance", FailureReason.TIMESTAMP_OUT_OF_TOLERANCE.code());
    assertEquals("signature_mismatch", FailureReason.SIGNATURE_MISMATCH.code());
  }

  // --- known-answer vector (computed independently with openssl and Python) ---

  @Test
  void matchesKnownAnswerVector() {
    String secret = "whsec_" + repeat("0123456789abcdef", 4);
    String body = "{\"event\":\"item/updated\",\"eventId\":\"evt_test\",\"itemId\":\"item_test\",\"note\":\"ação\"}";
    String v1 = "002c330ef76a9209f11870843509b1ac0f3df0f3b00b0a16712abdd0eff15a34";
    String header = "t=1763560800,v1=" + v1;
    Instant now = Instant.ofEpochSecond(1763560800L);

    assertEquals(v1, sign(secret, 1763560800L, body));
    assertValid(WebhookSignature.validate(body, header, Collections.singletonList(secret), 300, now));
    assertValid(WebhookSignature.validate(body.getBytes(StandardCharsets.UTF_8), header,
      Collections.singletonList(secret), 300, now));
  }

  // --- valid signatures ---

  @Test
  void acceptsValidSignature() {
    assertValid(validate(BODY, header(T), SECRET));
    assertTrue(WebhookSignature.verify(BODY, header(T), Collections.singletonList(SECRET), 300, NOW));
    assertTrue(WebhookSignature.verify(BODY.getBytes(StandardCharsets.UTF_8), header(T),
      Collections.singletonList(SECRET), 300, NOW));
  }

  @Test
  void acceptsStringAndBytePayloadAlike() {
    byte[] bytes = BODY.getBytes(StandardCharsets.UTF_8);
    assertValid(WebhookSignature.validate(bytes, header(T), Collections.singletonList(SECRET), 300, NOW));
    assertValid(WebhookSignature.validate(BODY, header(T), Collections.singletonList(SECRET), 300, NOW));
  }

  @Test
  void acceptsNonAsciiBody() {
    String body = "{\"description\":\"Pagamento à vista — café ☕ 日本\"}";
    String header = "t=" + T + ",v1=" + sign(SECRET, T, body);
    assertValid(validate(body, header, SECRET));
    assertValid(WebhookSignature.validate(body.getBytes(StandardCharsets.UTF_8), header,
      Collections.singletonList(SECRET), 300, NOW));
  }

  @Test
  void acceptsEmptyBody() {
    assertValid(validate("", "t=" + T + ",v1=" + sign(SECRET, T, ""), SECRET));
  }

  @Test
  void convenienceOverloadsUseTheCurrentTime() {
    long t = Instant.now().getEpochSecond();
    String fresh = "t=" + t + ",v1=" + sign(SECRET, t, BODY);
    String stale = "t=" + (t - 3600) + ",v1=" + sign(SECRET, t - 3600, BODY);

    assertTrue(WebhookSignature.verify(BODY, fresh, SECRET));
    assertTrue(WebhookSignature.verify(BODY.getBytes(StandardCharsets.UTF_8), fresh, SECRET));
    assertValid(WebhookSignature.validate(BODY, fresh, SECRET));
    assertFalse(WebhookSignature.verify(BODY, stale, SECRET));
    assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE,
      WebhookSignature.validate(BODY.getBytes(StandardCharsets.UTF_8), stale, SECRET));
    // a null "now" also means the current time
    assertValid(WebhookSignature.validate(BODY, fresh, Collections.singletonList(SECRET), 300, null));
  }

  // --- tampering and secrets ---

  @Test
  void rejectsBodyAlteredByOneByte() {
    byte[] altered = BODY.getBytes(StandardCharsets.UTF_8);
    altered[altered.length - 2] ^= 0x01;
    assertInvalid(FailureReason.SIGNATURE_MISMATCH, WebhookSignature.validate(altered, header(T),
      Collections.singletonList(SECRET), 300, NOW));
    assertInvalid(FailureReason.SIGNATURE_MISMATCH, validate(BODY + " ", header(T), SECRET));
  }

  @Test
  void rejectsReserializedBody() {
    String reserialized = BODY.replace(",", ", ");
    assertInvalid(FailureReason.SIGNATURE_MISMATCH, validate(reserialized, header(T), SECRET));
  }

  @Test
  void rejectsNullPayload() {
    assertInvalid(FailureReason.INVALID_PAYLOAD, validate(null, header(T), SECRET));
    assertInvalid(FailureReason.INVALID_PAYLOAD, WebhookSignature.validate((byte[]) null, header(T),
      Collections.singletonList(SECRET), 300, NOW));
    assertFalse(WebhookSignature.verify((String) null, header(T), SECRET));
  }

  @Test
  void rejectsWrongSecret() {
    assertInvalid(FailureReason.SIGNATURE_MISMATCH, validate(BODY, header(T), OTHER_SECRET));
  }

  @Test
  void usesTheWholeSecretPrefixIncluded() {
    assertInvalid(FailureReason.SIGNATURE_MISMATCH,
      validate(BODY, header(T), SECRET.substring("whsec_".length())));
  }

  @Test
  void acceptsAnySecretOfTheList() {
    assertValid(validate(BODY, header(T), OTHER_SECRET, SECRET));
    assertValid(validate(BODY, header(T), SECRET, OTHER_SECRET));
    assertInvalid(FailureReason.SIGNATURE_MISMATCH, validate(BODY, header(T), OTHER_SECRET, "", "whsec_x"));
  }

  @Test
  void skipsNullAndEmptySecrets() {
    assertValid(validate(BODY, header(T), null, SECRET, ""));
    assertTrue(WebhookSignature.verify(BODY, "t=" + Instant.now().getEpochSecond() + ",v1="
      + sign(SECRET, Instant.now().getEpochSecond(), BODY), null, SECRET));
  }

  @Test
  void reportsNoSecretWhenThereIsNoUsableSecret() {
    assertInvalid(FailureReason.NO_SECRET, validate(BODY, header(T)));
    assertInvalid(FailureReason.NO_SECRET, validate(BODY, header(T), ""));
    assertInvalid(FailureReason.NO_SECRET, validate(BODY, header(T), (String) null));
    assertInvalid(FailureReason.NO_SECRET, validate(BODY, header(T), null, ""));
    assertInvalid(FailureReason.NO_SECRET,
      WebhookSignature.validate(BODY, header(T), (List<String>) null, 300, NOW));
    assertInvalid(FailureReason.NO_SECRET, WebhookSignature.validate(BODY, header(T), (String[]) null));
    assertInvalid(FailureReason.NO_SECRET, WebhookSignature.validate(BODY, header(T)));
    // no_secret is checked first
    assertInvalid(FailureReason.NO_SECRET, validate(null, null));
  }

  // --- timestamp tolerance ---

  @Test
  void rejectsTimestampTooOld() {
    assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE, validateHeader(header(T - 301)));
  }

  @Test
  void rejectsTimestampTooFarInTheFuture() {
    assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE, validateHeader(header(T + 301)));
  }

  @Test
  void acceptsBothBoundariesInclusive() {
    assertValid(validateHeader(header(T - 300)));
    assertValid(validateHeader(header(T + 300)));
  }

  @Test
  void ignoresSubSecondPartOfNow() {
    Result result = WebhookSignature.validate(BODY, header(T - 300), Collections.singletonList(SECRET),
      300, NOW.plusMillis(999));
    assertValid(result);
  }

  @Test
  void honoursCustomTolerance() {
    assertValid(validateWithTolerance(header(T - 10), 10));
    assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE, validateWithTolerance(header(T - 11), 10));
    assertValid(validateWithTolerance(header(T), 0));
    assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE, validateWithTolerance(header(T - 1), 0));
    assertValid(validateWithTolerance(header(T - 86400), Long.MAX_VALUE));
  }

  @Test
  void fallsBackToDefaultForNegativeTolerance() {
    for (long tolerance : new long[] {-1, Long.MIN_VALUE}) {
      assertValid(validateWithTolerance(header(T - 300), tolerance));
      assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE, validateWithTolerance(header(T - 301), tolerance));
    }
  }

  @Test
  void checksTimestampBeforeSignature() {
    String header = "t=" + (T - 301) + ",v1=" + repeat("0", 64);
    assertInvalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE, validateHeader(header));
  }

  // --- header parsing ---

  @Test
  void reportsMissingOrEmptyHeader() {
    for (String value : new String[] {null, "", "   ", "\t"}) {
      assertInvalid(FailureReason.MISSING_HEADER, validateHeader(value));
    }
  }

  @Test
  void toleratesWhitespaceAroundPairsAndAnyOrder() {
    assertValid(validateHeader(" t=" + T + " , v1=" + V1 + " "));
    assertValid(validateHeader("v1=" + V1 + ",t=" + T));
  }

  @Test
  void acceptsUppercaseHexSignature() {
    assertValid(validateHeader("t=" + T + ",v1=" + V1.toUpperCase()));
  }

  @Test
  void ignoresUnknownPairs() {
    assertValid(validateHeader("t=" + T + ",v1=" + V1 + ",v2=" + repeat("b", 128)));
    assertValid(validateHeader("v0=abc,t=" + T + ",foo=bar=baz,v1=" + V1));
  }

  static Stream<Arguments> malformedHeaders() {
    byte[] signatureBytes = new byte[32];
    for (int i = 0; i < 32; i++) {
      signatureBytes[i] = (byte) Integer.parseInt(V1.substring(2 * i, 2 * i + 2), 16);
    }
    List<Arguments> cases = new ArrayList<>(Arrays.asList(
      Arguments.of("no pairs", "garbage"),
      Arguments.of("only t", "t=" + T),
      Arguments.of("only v1", "v1=" + V1),
      Arguments.of("empty pair", "t=" + T + ",,v1=" + V1),
      Arguments.of("trailing comma", "t=" + T + ",v1=" + V1 + ","),
      Arguments.of("leading comma", ",t=" + T + ",v1=" + V1),
      Arguments.of("pair without key", "t=" + T + ",=x,v1=" + V1),
      Arguments.of("pair without separator", "t=" + T + ",v1=" + V1 + ",flag"),
      Arguments.of("key with whitespace", "t=" + T + ",v 1=" + V1),
      Arguments.of("key with invalid characters", "t=" + T + ",v.1=x,v1=" + V1),
      Arguments.of("duplicate t", "t=" + T + ",t=" + T + ",v1=" + V1),
      Arguments.of("duplicate v1", "t=" + T + ",v1=" + V1 + ",v1=" + V1),
      Arguments.of("empty t", "t=,v1=" + V1),
      Arguments.of("negative t", "t=-" + T + ",v1=" + V1),
      Arguments.of("signed t", "t=+" + T + ",v1=" + V1),
      Arguments.of("decimal t", "t=" + T + ".5,v1=" + V1),
      Arguments.of("exponent t", "t=1.7e9,v1=" + V1),
      Arguments.of("hex t", "t=0x" + Long.toHexString(T) + ",v1=" + V1),
      Arguments.of("leading zero t", "t=0" + T + ",v1=" + V1),
      Arguments.of("zero t", "t=0,v1=" + V1),
      Arguments.of("absurd t", "t=" + repeat("9", 13) + ",v1=" + V1),
      Arguments.of("non-ASCII digits t", "t=١٧٦٣,v1=" + V1),
      Arguments.of("t with inner whitespace", "t= " + T + ",v1=" + V1),
      Arguments.of("milliseconds t", "t=" + (T * 1000) + ",v1=" + V1),
      Arguments.of("empty v1", "t=" + T + ",v1="),
      Arguments.of("non-hex v1", "t=" + T + ",v1=" + repeat("z", 64)),
      Arguments.of("short v1", "t=" + T + ",v1=" + V1.substring(0, 62)),
      Arguments.of("long v1", "t=" + T + ",v1=" + V1 + "00"),
      Arguments.of("odd-length v1", "t=" + T + ",v1=" + V1.substring(0, 63)),
      Arguments.of("base64 v1", "t=" + T + ",v1=" + Base64.getEncoder().encodeToString(signatureBytes)),
      Arguments.of("v1 with inner whitespace", "t=" + T + ",v1= " + V1)
    ));
    return cases.stream();
  }

  @ParameterizedTest(name = "rejects a malformed header: {0}")
  @MethodSource("malformedHeaders")
  void rejectsMalformedHeader(String name, String header) {
    assertInvalid(FailureReason.MALFORMED_HEADER, validateHeader(header));
    assertFalse(WebhookSignature.verify(BODY, header, Collections.singletonList(SECRET), 300, NOW));
  }

  @Test
  void neverThrowsOnHostileInput() {
    String[] headers = {
      repeat(",", 10000),
      repeat("t=1,", 5000),
      "t=" + T + ",v1=" + V1 + "," + repeat("x=y,", 10000) + "z=w",
      "\u0000t=" + T + ",v1=" + V1,
      "t=" + T + ",v1=" + V1 + "\u0000",
      "t=" + T + "\n,v1=" + V1,
      "=",
      "==,==",
    };
    for (String header : headers) {
      Result result = validateHeader(header);
      assertTrue(result.getReason() != null || result.isValid());
    }
  }

  // --- result type ---

  @Test
  void resultIsAValueType() {
    assertEquals(validate(BODY, header(T), SECRET), validate(BODY, header(T), SECRET));
    assertEquals(validate(BODY, header(T), OTHER_SECRET), validate(BODY + " ", header(T), SECRET));
    assertEquals(validate(BODY, header(T), SECRET).hashCode(), validate(BODY, header(T), SECRET).hashCode());
    assertEquals("Result{valid}", validate(BODY, header(T), SECRET).toString());
    assertEquals("Result{invalid: signature_mismatch}", validate(BODY, header(T), OTHER_SECRET).toString());
  }

  @Test
  void doesNotModifyThePayload() {
    byte[] payload = BODY.getBytes(StandardCharsets.UTF_8);
    byte[] copy = payload.clone();
    WebhookSignature.validate(payload, header(T), Collections.singletonList(SECRET), 300, NOW);
    assertArrayEquals(copy, payload);
  }
}
