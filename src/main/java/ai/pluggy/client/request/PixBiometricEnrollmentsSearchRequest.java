package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotNull;

import ai.pluggy.client.response.PixBiometricEnrollmentStatus;
import java.util.HashMap;

public class PixBiometricEnrollmentsSearchRequest extends HashMap<String, Object> {

  /**
   * @param customerId Filter by Payment Customer.
   * @return this instance, useful to continue adding params
   */
  public PixBiometricEnrollmentsSearchRequest customerId(String customerId) {
    assertNotNull(customerId, "customerId");
    put("customerId", customerId);
    return this;
  }

  /**
   * @param cpf Filter by payer CPF, digits only: an enrollment returns it masked, so a returned
   *     value cannot be passed back.
   * @return this instance, useful to continue adding params
   */
  public PixBiometricEnrollmentsSearchRequest cpf(String cpf) {
    assertNotNull(cpf, "cpf");
    put("cpf", cpf);
    return this;
  }

  /**
   * @param cnpj Filter by payer CNPJ, digits only: an enrollment returns it masked, so a returned
   *     value cannot be passed back.
   * @return this instance, useful to continue adding params
   */
  public PixBiometricEnrollmentsSearchRequest cnpj(String cnpj) {
    assertNotNull(cnpj, "cnpj");
    put("cnpj", cnpj);
    return this;
  }

  /**
   * @param status Filter by enrollment status.
   * @return this instance, useful to continue adding params
   */
  public PixBiometricEnrollmentsSearchRequest status(PixBiometricEnrollmentStatus status) {
    assertNotNull(status, "status");
    put("status", status.getValue());
    return this;
  }

  /**
   * @param page Integer - page number to fetch, starting at page=1.
   * @return this instance, useful to continue adding params
   */
  public PixBiometricEnrollmentsSearchRequest page(Integer page) {
    assertNotNull(page, "page");
    put("page", page);
    return this;
  }

  /**
   * @param pageSize Integer - page size value, indicates max items to fetch per page.
   * @return this instance, useful to continue adding params
   */
  public PixBiometricEnrollmentsSearchRequest pageSize(Integer pageSize) {
    assertNotNull(pageSize, "pageSize");
    put("pageSize", pageSize);
    return this;
  }
}
