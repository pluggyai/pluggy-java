package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

/**
 * One resource the financial institution declared for an item's Open Finance consent, reported
 * verbatim.
 */
@Data
@Builder
public class ItemResource {

  /** The institution's identifier for the resource. */
  String resourceId;

  /**
   * Open Finance resource type, documented as {@code ACCOUNT}, {@code CREDIT_CARD_ACCOUNT},
   * {@code LOAN}, {@code FINANCING}, {@code UNARRANGED_ACCOUNT_OVERDRAFT},
   * {@code INVOICE_FINANCING}, {@code BANK_FIXED_INCOME}, {@code CREDIT_FIXED_INCOME},
   * {@code VARIABLE_INCOME}, {@code TREASURE_TITLE} or {@code FUND}.
   *
   * <p>A String rather than an enum: other values can appear, and an enum would turn them into
   * null instead of passing them through.
   */
  String type;

  ItemResourceStatus status;
}
