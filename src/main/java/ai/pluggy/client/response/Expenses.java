package ai.pluggy.client.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {
  String id;
  String transactionId;
  Double serviceTax;
  Double brokerageFee;
  Double incomeTax;
  Double other;
  Double tradingAssetsNoticeFee;
  Double maintenanceFee;
  Double settlementFee;
  Double clearingFee;
  Double stockExchangeFee;
  Double custodyFee;
  Double operatingFee;
}
