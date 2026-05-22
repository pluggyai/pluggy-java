package ai.pluggy.client.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Nationality {

  Boolean hasBrazilianNationality;
  List<OtherNationality> otherNationalities;
}
