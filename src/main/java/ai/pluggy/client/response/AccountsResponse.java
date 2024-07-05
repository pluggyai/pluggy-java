package ai.pluggy.client.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountsResponse {

  List<Account> results;
}
