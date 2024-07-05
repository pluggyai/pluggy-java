package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class InvestmentsResponse {

  List<Investment> results;
}
