package ai.pluggy.client.response;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NationalityDocument {

  String type;
  String number;
  String country;
  Date issueDate;
  Date expirationDate;
  String additionalInfo;
}
