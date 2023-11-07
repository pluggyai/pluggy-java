package ai.pluggy.client.response;

import java.util.List;
import lombok.Data;

@Data
public class AccountsResponse {

  List<Account> results;
}
