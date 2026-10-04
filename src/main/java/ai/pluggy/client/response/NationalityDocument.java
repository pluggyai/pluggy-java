package ai.pluggy.client.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationalityDocument {

  String type;
  String number;
  String country;
  Date issueDate;
  Date expirationDate;
  String additionalInfo;
}
