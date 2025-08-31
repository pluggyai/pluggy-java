package ai.pluggy.client.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BillsResponse {

  List<Bill> results;
}
