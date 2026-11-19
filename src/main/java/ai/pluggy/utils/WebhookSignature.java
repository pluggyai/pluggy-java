package ai.pluggy.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies the {@code X-Pluggy-Signature} header of Pluggy webhook notifications.
 *
 * <p>The header has the form {@code t=<unix timestamp in seconds>,v1=<hex HMAC-SHA256>}, where
 * {@code v1} is computed with the signing secret shown in the Pluggy Dashboard (used as-is,
 * {@code whsec_} prefix included) over {@code <t>.<raw request body>}. Always verify the
 * <b>raw</b> body, exactly as received, before any JSON parsing: a re-serialized body does not
 * match.
 *
 * <p>Several secrets can be passed, so the old and the new one are both accepted while a secret
 * is being rotated. {@code null} and empty entries are skipped. Unknown {@code key=value} pairs
 * in the header are ignored, and the signature comparison is constant-time.
 *
 * <p>None of these methods throw on malformed input: {@link #verify(byte[], String, String...)}
 * returns {@code false} and {@link #validate(byte[], String, String...)} returns a
 * {@link Result} with the {@link FailureReason}.
 *
 * <pre>{@code
 * boolean valid = WebhookSignature.verify(
 *   rawBody,                                          // byte[] or String, before JSON parsing
 *   request.getHeader(WebhookSignature.HEADER_NAME),
 *   System.getenv("PLUGGY_WEBHOOK_SECRET"),
 *   System.getenv("PLUGGY_WEBHOOK_SECRET_OLD"));      // unset during normal operation: skipped
 * if (!valid) {
 *   // answer 401
 * }
 * }</pre>
 *
 * @see <a href="https://docs.pluggy.ai/docs/developer-tools/webhook-security">Webhook security</a>
 */
public final class WebhookSignature {

  /**
   * Name of the HTTP header that carries the webhook signature. HTTP header names are
   * case-insensitive, and so are the lookups of the Servlet API and Spring.
   */
  public static final String HEADER_NAME = "X-Pluggy-Signature";

  /** Default maximum age (and clock skew), in seconds, accepted for a signature timestamp. */
  public static final long DEFAULT_TOLERANCE_SECONDS = 300L;

  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private static final int SIGNATURE_BYTES = 32;
  // 1 to 12 digits, no sign and no leading zero: covers every timestamp until the year 33658.
  private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("[1-9][0-9]{0,11}");
  private static final Pattern V1_PATTERN = Pattern.compile("[0-9a-fA-F]{64}");
  private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

  private WebhookSignature() {
  }

  /** Why a webhook signature was rejected. */
  public enum FailureReason {
    /** No usable signing secret was provided (none, or only null / empty entries). */
    NO_SECRET,
    /** The payload is null. */
    INVALID_PAYLOAD,
    /** The signature header is absent, empty or blank. */
    MISSING_HEADER,
    /** The signature header could not be parsed (bad format, bad timestamp or bad {@code v1}). */
    MALFORMED_HEADER,
    /** The signature timestamp is too far in the past or in the future. */
    TIMESTAMP_OUT_OF_TOLERANCE,
    /** The signature does not match the payload with any of the given secrets. */
    SIGNATURE_MISMATCH;

    /**
     * @return the reason as a lowercase code ({@code no_secret}, {@code invalid_payload},
     *     {@code missing_header}, {@code malformed_header}, {@code timestamp_out_of_tolerance},
     *     {@code signature_mismatch}), the same codes the other Pluggy SDKs report
     */
    public String code() {
      return name().toLowerCase(Locale.ROOT);
    }
  }

  /** Outcome of {@link #validate(byte[], String, String...)}. Immutable. */
  public static final class Result {

    private static final Result VALID = new Result(null);

    private final FailureReason reason;

    private Result(FailureReason reason) {
      this.reason = reason;
    }

    private static Result invalid(FailureReason reason) {
      return new Result(reason);
    }

    /** @return true when the signature is valid */
    public boolean isValid() {
      return reason == null;
    }

    /** @return why the signature was rejected, or null when it is valid */
    public FailureReason getReason() {
      return reason;
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Result && ((Result) o).reason == reason;
    }

    @Override
    public int hashCode() {
      return reason == null ? 0 : reason.hashCode();
    }

    @Override
    public String toString() {
      return reason == null ? "Result{valid}" : "Result{invalid: " + reason.code() + "}";
    }
  }

  /**
   * Verifies a webhook signature with the default tolerance (5 minutes) and the current time.
   *
   * @param payload the raw request body, exactly as received
   * @param header the value of the {@code X-Pluggy-Signature} header (may be null)
   * @param secrets one or more signing secrets; null and empty entries are skipped
   * @return true only when the header parses, its timestamp is within the tolerance and the
   *     signature matches one of the secrets
   */
  public static boolean verify(byte[] payload, String header, String... secrets) {
    return validate(payload, header, secrets).isValid();
  }

  /**
   * Same as {@link #verify(byte[], String, String...)}, for a body read as a String. The String
   * is encoded as UTF-8; it must be the body exactly as received.
   */
  public static boolean verify(String payload, String header, String... secrets) {
    return validate(payload, header, secrets).isValid();
  }

  /**
   * Verifies a webhook signature.
   *
   * @param payload the raw request body, exactly as received
   * @param header the value of the {@code X-Pluggy-Signature} header (may be null)
   * @param secrets the signing secrets; null and empty entries are skipped
   * @param toleranceSeconds maximum difference, in seconds, between the signature timestamp and
   *     {@code now}, in either direction (inclusive); a negative value falls back to
   *     {@link #DEFAULT_TOLERANCE_SECONDS}
   * @param now the current time; null means {@link Instant#now()}
   * @return true only when the signature is valid
   */
  public static boolean verify(byte[] payload, String header, Iterable<String> secrets,
    long toleranceSeconds, Instant now) {
    return validate(payload, header, secrets, toleranceSeconds, now).isValid();
  }

  /** Same as {@link #verify(byte[], String, Iterable, long, Instant)}, for a String body. */
  public static boolean verify(String payload, String header, Iterable<String> secrets,
    long toleranceSeconds, Instant now) {
    return validate(payload, header, secrets, toleranceSeconds, now).isValid();
  }

  /**
   * Validates a webhook signature with the default tolerance (5 minutes) and the current time,
   * and tells why it was rejected.
   *
   * @param payload the raw request body, exactly as received
   * @param header the value of the {@code X-Pluggy-Signature} header (may be null)
   * @param secrets one or more signing secrets; null and empty entries are skipped
   * @return the result; never null
   */
  public static Result validate(byte[] payload, String header, String... secrets) {
    return validate(payload, header, asList(secrets), DEFAULT_TOLERANCE_SECONDS, null);
  }

  /** Same as {@link #validate(byte[], String, String...)}, for a String body (UTF-8). */
  public static Result validate(String payload, String header, String... secrets) {
    return validate(payload, header, asList(secrets), DEFAULT_TOLERANCE_SECONDS, null);
  }

  /** Same as {@link #validate(byte[], String, Iterable, long, Instant)}, for a String body. */
  public static Result validate(String payload, String header, Iterable<String> secrets,
    long toleranceSeconds, Instant now) {
    byte[] body = payload == null ? null : payload.getBytes(StandardCharsets.UTF_8);
    return validate(body, header, secrets, toleranceSeconds, now);
  }

  /**
   * Validates a webhook signature and tells why it was rejected.
   *
   * <p>Checks run in this order: {@link FailureReason#NO_SECRET}, {@link
   * FailureReason#INVALID_PAYLOAD}, {@link FailureReason#MISSING_HEADER}, {@link
   * FailureReason#MALFORMED_HEADER}, {@link FailureReason#TIMESTAMP_OUT_OF_TOLERANCE}, {@link
   * FailureReason#SIGNATURE_MISMATCH}.
   *
   * @param payload the raw request body, exactly as received
   * @param header the value of the {@code X-Pluggy-Signature} header (may be null)
   * @param secrets the signing secrets; null and empty entries are skipped
   * @param toleranceSeconds maximum difference, in seconds, between the signature timestamp and
   *     {@code now}, in either direction (inclusive); a negative value falls back to
   *     {@link #DEFAULT_TOLERANCE_SECONDS}
   * @param now the current time; null means {@link Instant#now()}
   * @return the result; never null
   */
  public static Result validate(byte[] payload, String header, Iterable<String> secrets,
    long toleranceSeconds, Instant now) {
    try {
      List<String> usableSecrets = usableSecrets(secrets);
      if (usableSecrets.isEmpty()) {
        return Result.invalid(FailureReason.NO_SECRET);
      }
      if (payload == null) {
        return Result.invalid(FailureReason.INVALID_PAYLOAD);
      }
      if (header == null || header.trim().isEmpty()) {
        return Result.invalid(FailureReason.MISSING_HEADER);
      }

      ParsedHeader parsed = parseHeader(header);
      if (parsed == null) {
        return Result.invalid(FailureReason.MALFORMED_HEADER);
      }

      long tolerance = toleranceSeconds >= 0 ? toleranceSeconds : DEFAULT_TOLERANCE_SECONDS;
      long nowSeconds = (now != null ? now : Instant.now()).getEpochSecond();
      if (Math.abs(nowSeconds - parsed.timestamp) > tolerance) {
        return Result.invalid(FailureReason.TIMESTAMP_OUT_OF_TOLERANCE);
      }

      // Check every secret, without stopping at the first match, so the time taken does not
      // depend on which secret matched.
      boolean matched = false;
      for (String secret : usableSecrets) {
        byte[] expected = computeSignature(secret, parsed.rawTimestamp, payload);
        if (expected.length == SIGNATURE_BYTES && MessageDigest.isEqual(expected, parsed.signature)) {
          matched = true;
        }
      }
      return matched ? Result.VALID : Result.invalid(FailureReason.SIGNATURE_MISMATCH);
    } catch (GeneralSecurityException | RuntimeException e) {
      return Result.invalid(FailureReason.MALFORMED_HEADER);
    }
  }

  private static final class ParsedHeader {
    private final long timestamp;
    private final String rawTimestamp;
    private final byte[] signature;

    private ParsedHeader(long timestamp, String rawTimestamp, byte[] signature) {
      this.timestamp = timestamp;
      this.rawTimestamp = rawTimestamp;
      this.signature = signature;
    }
  }

  private static ParsedHeader parseHeader(String header) {
    String rawTimestamp = null;
    String rawSignature = null;

    // limit -1 keeps trailing empty strings, so a trailing comma is rejected as an empty pair
    for (String rawPart : header.split(",", -1)) {
      String part = rawPart.trim();
      int separator = part.indexOf('=');
      if (separator <= 0) {
        return null;
      }
      String key = part.substring(0, separator);
      String value = part.substring(separator + 1);
      if (!KEY_PATTERN.matcher(key).matches()) {
        return null;
      }
      if ("t".equals(key)) {
        if (rawTimestamp != null) {
          return null;
        }
        rawTimestamp = value;
      } else if ("v1".equals(key)) {
        if (rawSignature != null) {
          return null;
        }
        rawSignature = value;
      }
      // Unknown keys (e.g. a future "v2=") are ignored on purpose.
    }

    if (rawTimestamp == null || rawSignature == null) {
      return null;
    }
    if (!TIMESTAMP_PATTERN.matcher(rawTimestamp).matches()
      || !V1_PATTERN.matcher(rawSignature).matches()) {
      return null;
    }
    return new ParsedHeader(Long.parseLong(rawTimestamp), rawTimestamp, decodeHex(rawSignature));
  }

  private static byte[] computeSignature(String secret, String rawTimestamp, byte[] payload)
    throws GeneralSecurityException {
    Mac mac = Mac.getInstance(HMAC_ALGORITHM);
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
    mac.update((rawTimestamp + ".").getBytes(StandardCharsets.UTF_8));
    mac.update(payload);
    return mac.doFinal();
  }

  private static byte[] decodeHex(String hex) {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      int high = Character.digit(hex.charAt(2 * i), 16);
      int low = Character.digit(hex.charAt(2 * i + 1), 16);
      bytes[i] = (byte) ((high << 4) | low);
    }
    return bytes;
  }

  private static List<String> asList(String[] secrets) {
    return secrets == null ? Collections.<String>emptyList() : Arrays.asList(secrets);
  }

  private static List<String> usableSecrets(Iterable<String> secrets) {
    List<String> usable = new ArrayList<>();
    if (secrets != null) {
      for (String secret : secrets) {
        if (secret != null && !secret.isEmpty()) {
          usable.add(secret);
        }
      }
    }
    return usable;
  }
}
