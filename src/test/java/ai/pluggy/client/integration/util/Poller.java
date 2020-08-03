package ai.pluggy.client.integration.util;

import java.util.Date;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Poller {

  /**
   * Util method to run a request periodically until specified condition is met, or
   * max timeout is exceeded.
   *
   * @param requestRunner     - entity response supplier, that will be used to test for the condition predicate.
   * @param expectedCondition - predicate that will test each runner result, until it's fullfiled
   * @param pollIntervalMs    - base interval MS. However it's always increased by a 1.5 factor to prevent overloading the server.
   * @param maxTimeoutMs      - maximum timeout to shortcut the execution, in case the condition takes way too long or doesn't resolve at all.
   * @param <T>               - the instance type of the requestRunner response
   */
  public static <T> void pollRequestUntil(Supplier<T> requestRunner, Predicate<T> expectedCondition,
    Integer pollIntervalMs, Integer maxTimeoutMs) throws InterruptedException {
    Date startTime = new Date();
    Integer tries = 0;
    log.debug("Polling request, interval ms: " + pollIntervalMs + ", max timeout: " + maxTimeoutMs
      + "ms...");
    T result = requestRunner.get();
    long elapsedTime = 0;
    while (!expectedCondition.test(result)) {
      elapsedTime = new Date().getTime() - startTime.getTime();
      if (elapsedTime > maxTimeoutMs) {
        throw new RuntimeException(
          "Timeout exceeded after " + tries + " tries (" + elapsedTime + "ms)");
      }
      log.debug("Condition not met after attempt #" + tries + ", (elapsed: " + elapsedTime
        + "ms) retrying in " + pollIntervalMs + "ms...");
      pollIntervalMs = (int) Math.floor(pollIntervalMs * 1.5f); // exponential backoff
      Thread.sleep(pollIntervalMs);
      result = requestRunner.get();
      tries++;
    }
    elapsedTime = new Date().getTime() - startTime.getTime();
    log.debug("Condition met after " + tries + " attempts, in " + elapsedTime + "ms");
  }

}
