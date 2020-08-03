package ai.pluggy.client.response;

import java.util.List;
import lombok.Data;

@Data
public class TransactionsResponse {

  Integer total;
  Integer totalPages;
  Integer page;
  List<Transaction> results;
}
