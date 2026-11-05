package ai.pluggy.client.unit;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Small HTTP server bound to the loopback interface, for behaviour that only exists below OkHttp's
 * application interceptors, such as following redirects. No request leaves the machine.
 */
final class FakePluggyServer implements AutoCloseable {

  /** 2100-01-01T00:00:00Z, as a JWT {@code exp} claim. */
  static final long FAR_FUTURE = 4102444800L;

  private final HttpServer server;
  private final Map<String, Function<Received, Reply>> routes = new ConcurrentHashMap<>();
  private final List<Received> received = new CopyOnWriteArrayList<>();

  FakePluggyServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext("/", this::handle);
    server.start();
  }

  /** Builds a token shaped like the API key: only the payload is meaningful to the SDK. */
  static String jwt(String subject, long expiresAtEpochSeconds) {
    String payload = "{\"sub\":\"" + subject + "\",\"exp\":" + expiresAtEpochSeconds + "}";
    return "header."
      + Base64.getUrlEncoder().withoutPadding()
      .encodeToString(payload.getBytes(StandardCharsets.UTF_8))
      + ".signature";
  }

  void on(String method, String path, Function<Received, Reply> handler) {
    routes.put(method + " " + path, handler);
  }

  String url() {
    return "http://127.0.0.1:" + server.getAddress().getPort();
  }

  List<Received> received() {
    return new ArrayList<>(received);
  }

  List<Received> received(String path) {
    return received.stream().filter(request -> request.path.equals(path))
      .collect(Collectors.toList());
  }

  /** The x-api-key value of each request to the given path, in arrival order. */
  List<String> apiKeysSentTo(String path) {
    return received(path).stream().map(Received::apiKey).collect(Collectors.toList());
  }

  @Override
  public void close() {
    server.stop(0);
  }

  private void handle(HttpExchange exchange) throws IOException {
    Received request = new Received(
      exchange.getRequestMethod(),
      exchange.getRequestURI().getPath(),
      exchange.getRequestHeaders(),
      readBody(exchange.getRequestBody()));
    received.add(request);

    Function<Received, Reply> handler = routes.get(request.method + " " + request.path);
    Reply reply = handler == null ? Reply.json(404, "{}") : handler.apply(request);

    if (reply.location != null) {
      exchange.getResponseHeaders().set("Location", reply.location);
    }
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    byte[] body = reply.body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(reply.status, body.length == 0 ? -1 : body.length);
    if (body.length > 0) {
      try (OutputStream output = exchange.getResponseBody()) {
        output.write(body);
      }
    }
    exchange.close();
  }

  private static String readBody(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] buffer = new byte[4096];
    int read;
    while ((read = input.read(buffer)) != -1) {
      output.write(buffer, 0, read);
    }
    return new String(output.toByteArray(), StandardCharsets.UTF_8);
  }

  /** A request as the server actually received it. */
  static final class Received {

    final String method;
    final String path;
    final String body;
    private final Headers headers;

    private Received(String method, String path, Headers headers, String body) {
      this.method = method;
      this.path = path;
      this.headers = headers;
      this.body = body;
    }

    String apiKey() {
      return headers.getFirst("x-api-key");
    }

    List<String> headerValues(String name) {
      return headers.get(name);
    }
  }

  /** What the server answers with. */
  static final class Reply {

    final int status;
    final String body;
    final String location;

    private Reply(int status, String body, String location) {
      this.status = status;
      this.body = body;
      this.location = location;
    }

    static Reply json(int status, String body) {
      return new Reply(status, body, null);
    }

    static Reply redirect(int status, String location) {
      return new Reply(status, "", location);
    }

    static Reply apiKey(String apiKey) {
      return json(200, "{\"apiKey\":\"" + apiKey + "\"}");
    }
  }
}
