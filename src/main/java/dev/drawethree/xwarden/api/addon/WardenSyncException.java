package dev.drawethree.xwarden.api.addon;

/**
 * Thrown when work handed to the server thread did not complete.
 *
 * <p>Either the task itself threw - the cause says what - or the server did not get to it in
 * reasonable time, which in practice means it is shutting down. Unchecked, so a request handler can
 * let it escape and turn it into a failure the caller understands rather than swallowing it and
 * returning something empty that looks like an answer.
 *
 * @since 1.0.0
 */
public class WardenSyncException extends RuntimeException {

    /**
     * A failure with no underlying cause, such as the server not running the task in time.
     *
     * @param message what went wrong
     * @since 1.0.0
     */
    public WardenSyncException(String message) {
        super(message);
    }

    /**
     * A failure caused by the task itself throwing.
     *
     * @param message what went wrong
     * @param cause   what the task threw
     * @since 1.0.0
     */
    public WardenSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
