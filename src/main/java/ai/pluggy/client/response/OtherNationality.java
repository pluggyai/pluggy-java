package ai.pluggy.client.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtherNationality {

  /** Country code in alpha3 ISO-3166 format. */
  String countryCode;
  List<NationalityDocument> documents;
}
