package ai.pluggy.client.response;

import lombok.Data;

@Data
public class Merchant {
  String name;
  String businessName;
  String cnpj;
  String cnae;
  String category;
}
