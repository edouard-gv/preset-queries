package net.koffeepot.presetqueries.common;

public class AuthException extends TechnicalRuntimeException {
    public AuthException(String message) {
        super(message);
    }

    public AuthException(Exception ex) {
        super(ex);
    }
}
