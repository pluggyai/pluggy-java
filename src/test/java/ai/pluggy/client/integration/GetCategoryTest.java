package ai.pluggy.client.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.pluggy.client.response.CategoriesResponse;
import ai.pluggy.client.response.Category;
import ai.pluggy.client.response.ErrorResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public class GetCategoryTest extends BaseApiIntegrationTest {

  @Test
  void getCategory_byExistingId_ok() throws IOException {
    // precondition: getCategories data to get an id
    Response<CategoriesResponse> categoriesResponse = client.service().getCategories().execute();
    CategoriesResponse categories = categoriesResponse.body();
    
    assertNotNull(categories);
    assertTrue(categories.getResults().size() > 0);
    
    // get existing category id 
    String existingCategoryId = categories.getResults().get(0).getId();
    Response<Category> categoryResponse = client.service().getCategory(existingCategoryId).execute();
    Category category = categoryResponse.body();
    
    // expect requested category to exist and to match the requested id
    assertNotNull(category);
    assertEquals(category.getId(), existingCategoryId);
  }

  @Test
  void getCategory_byInexistentId_responseError404() throws IOException {
    String inexistentCategoryId = "1234567890";
    Response<Category> categoryResponse = client.service().getCategory(inexistentCategoryId).execute();
    Category category = categoryResponse.body();
    assertNull(category);

    // expect 404 error response
    ErrorResponse errorResponse = client.parseError(categoryResponse);
    assertNotNull(errorResponse);
    assertEquals(errorResponse.getCode(), 404);
  }

}
