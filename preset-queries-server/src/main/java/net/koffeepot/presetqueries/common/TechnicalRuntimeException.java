package net.koffeepot.presetqueries.common;

public class TechnicalRuntimeException extends RuntimeException {

    public TechnicalRuntimeException(String message) {
        super(message);
    }

    public TechnicalRuntimeException(Exception ex) {
        super(ex);
    }
}
