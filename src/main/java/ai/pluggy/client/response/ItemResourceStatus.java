package ai.pluggy.client.response;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * What the financial institution reports about one resource of an item's Open Finance consent.
 *
 * <p>{@code PENDING_AUTHORISATION} keeps Open Finance's British spelling: the user still has to
 * approve the resource at their bank. A value added server-side after this SDK release
 * deserializes as {@code null}.
 */
@AllArgsConstructor
public enum ItemResourceStatus {

  @SerializedName("AVAILABLE")
  AVAILABLE("AVAILABLE"),

  @SerializedName("UNAVAILABLE")
  UNAVAILABLE("UNAVAILABLE"),

  @SerializedName("TEMPORARILY_UNAVAILABLE")
  TEMPORARILY_UNAVAILABLE("TEMPORARILY_UNAVAILABLE"),

  @SerializedName("PENDING_AUTHORISATION")
  PENDING_AUTHORISATION("PENDING_AUTHORISATION");

  @Getter
  private String value;
}
