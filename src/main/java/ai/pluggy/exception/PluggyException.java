package ai.pluggy.exception;

import java.io.IOException;

public class PluggyException extends IOException {

    public PluggyException(String message) {
        super(message);
    }

    public PluggyException(String message, Throwable cause) {
        super(message, cause);
    }
}
