package ai.pluggy.client.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.Account;
import ai.pluggy.client.response.AccountBalance;
import ai.pluggy.client.response.AccountsResponse;
import ai.pluggy.client.response.Bill;
import ai.pluggy.client.response.Connector;
import ai.pluggy.client.response.CredentialSelectOption;
import ai.pluggy.client.response.IdentityResponse;
import ai.pluggy.client.response.Investment;
import ai.pluggy.client.response.ItemResponse;
import ai.pluggy.client.response.SmartTransferPayment;
import ai.pluggy.client.response.SmartTransferPreauthorization;
import ai.pluggy.client.response.SmartTransferPreauthorizationBalance;
import ai.pluggy.client.response.TransactionsCursorResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Response classes built with Lombok's @Builder must keep a public no-args constructor, so that
 * JSON mappers that require one (e.g. Jackson) can deserialize them, not only Gson.
 */
public class BuilderNoArgsConstructorTest {

  private static final List<Class<?>> RESPONSE_CLASSES = Arrays.asList(
    Account.class, AccountBalance.class, AccountsResponse.class, Bill.class, Connector.class,
    CredentialSelectOption.class, IdentityResponse.class, Investment.class, ItemResponse.class,
    SmartTransferPayment.class, SmartTransferPreauthorization.class,
    SmartTransferPreauthorizationBalance.class, TransactionsCursorResponse.class);

  @Test
  void builderResponseClasses_havePublicNoArgsConstructor() throws Exception {
    for (Class<?> clazz : RESPONSE_CLASSES) {
      Constructor<?> constructor = clazz.getDeclaredConstructor();
      assertTrue(Modifier.isPublic(constructor.getModifiers()), clazz.getSimpleName());
      assertNotNull(constructor.newInstance(), clazz.getSimpleName());
    }
  }

  @Test
  void builderStillWorksAlongsideNoArgsConstructor() {
    Account account = Account.builder().id("acc-1").balance(10.0).build();
    assertEquals("acc-1", account.getId());
    Account empty = new Account();
    empty.setId("acc-2");
    assertEquals("acc-2", empty.getId());
  }
}
