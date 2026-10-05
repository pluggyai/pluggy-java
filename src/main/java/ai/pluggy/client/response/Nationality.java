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
public class Nationality {

  Boolean hasBrazilianNationality;
  List<OtherNationality> otherNationalities;
}
