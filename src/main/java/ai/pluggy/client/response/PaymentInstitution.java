package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Institution that holds a payment recipient's bank account. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstitution {

  String id;
  String name;
  String tradeName;
  String ispb;
  String compe;
  Date createdAt;
  Date updatedAt;
}
