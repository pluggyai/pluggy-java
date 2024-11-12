package ai.pluggy.client.response;

import lombok.Data;

@Data
public class BoletoMetadata {
  String digitableLine;
  String barcode;
  Double baseAmount;
  Double interestAmount;
  Double penaltyAmount;
  Double discountAmount;
}
