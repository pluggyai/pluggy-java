package ai.pluggy.client.request;

import static ai.pluggy.utils.Asserts.assertNotNull;

import java.util.HashMap;

public class PaymentIntentsSearchRequest extends HashMap<String, Object> {

  /**
   * @param paymentRequestId Filter by payment request.
   * @return this instance, useful to continue adding params
   */
  public PaymentIntentsSearchRequest paymentRequestId(String paymentRequestId) {
    assertNotNull(paymentRequestId, "paymentRequestId");
    put("paymentRequestId", paymentRequestId);
    return this;
  }

  /**
   * @param enrollmentId Filter by Pix por biometria linked account.
   * @return this instance, useful to continue adding params
   */
  public PaymentIntentsSearchRequest enrollmentId(String enrollmentId) {
    assertNotNull(enrollmentId, "enrollmentId");
    put("enrollmentId", enrollmentId);
    return this;
  }
}
