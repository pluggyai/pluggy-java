package ai.pluggy.client.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherNationality {

  /** Country code in alpha3 ISO-3166 format. */
  String countryCode;
  List<NationalityDocument> documents;
}
