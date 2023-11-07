package ai.pluggy.client.response;

import lombok.Data;
import java.util.List;

@Data
public class InvestmentsResponse {

  List<Investment> results;
}
