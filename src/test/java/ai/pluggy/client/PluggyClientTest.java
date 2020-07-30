package ai.pluggy.client;

import ai.pluggy.exception.PluggyException;
import org.junit.Test;

public class PluggyClientTest {
    private static final String CLIENT_ID = "906a15c0-fdde-4dc5-9a23-df44455e1fb4";
    private static final String CLIENT_SECRET = "6fc93aec-9166-417c-8363-669167a39ce4";

    @Test
    public void shouldAuthenticateAPI() throws PluggyException {
        PluggyClient pluggy = new PluggyClient(CLIENT_ID, CLIENT_SECRET);

    }

}
