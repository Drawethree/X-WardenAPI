package dev.drawethree.xwarden.api.flags;

import java.util.Locale;

/**
 * What X-Warden does when a check reaches its violation threshold, as configured per check under
 * {@code action:} in {@code warden.yml}.
 *
 * <p>Every statistical check ships on {@link #ALERT}. Only proof - a duplicated item, a cheat
 * client answering a probe - ships higher, and the configuration says so where it does.
 *
 * @since 1.0.0
 */
public enum WardenAction {

    /** Write it to the log and nothing else. */
    LOG(0),
    /** Tell staff holding the alerts permission, and post to Discord if configured. */
    ALERT(1),
    /** Run the commands configured on the check, as the console. */
    COMMAND(2),
    /**
     * Applies the punishment preset named on the check ({@code preset:}), stepping up its ladder
     * the way a staff member would from the menu. A check with no preset alerts and does nothing
     * more, and the console says so.
     *
     * @since 1.1.0
     */
    PUNISH(3),
    /** Hold the player where they are, for the configured number of minutes. */
    FREEZE(4),
    /** Disconnect the player with the configured message. */
    KICK(5),
    /** Ban the player through the server's ban list. */
    BAN(6);

    private final int severity;

    WardenAction(int severity) {
        this.severity = severity;
    }

    /**
     * Where this action sits in the ladder, {@link #LOG} lowest.
     *
     * @return the rank, 0 - 6
     * @since 1.0.0
     */
    public int getSeverity() {
        return this.severity;
    }

    /**
     * Whether this action does something to the player rather than only telling staff.
     *
     * @return {@code true} for {@link #COMMAND} and everything above it
     * @since 1.0.0
     */
    public boolean isPunitive() {
        return this.severity >= COMMAND.severity;
    }

    /**
     * Reads an action from configuration text.
     *
     * @param raw      the text, in any case
     * @param fallback what to answer for blank or unknown text
     * @return the action
     * @since 1.0.0
     */
    public static WardenAction parse(String raw, WardenAction fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unknown) {
            return fallback;
        }
    }
}
