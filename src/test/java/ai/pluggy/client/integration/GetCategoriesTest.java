package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.CategoriesResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetCategoriesTest extends BaseApiIntegrationTest {

  @Test
  void getCategories_ok() throws IOException {
    Response<CategoriesResponse> categoriesResponse = client.service().getCategories().execute();
    CategoriesResponse categories = categoriesResponse.body();

    assertNotNull(categories);
    assertTrue(categories.getResults().size() > 0);
  }
}
