package dev.drawethree.xwarden.api.flags;

import java.util.Locale;

public enum WardenAction {

    LOG(0),
    ALERT(1),
    COMMAND(2),
    /**
     * Applies the punishment preset named on the check ({@code preset:}), stepping up its ladder
     * the way a staff member would from the menu. A check with no preset falls back to ALERT.
     */
    PUNISH(3),
    FREEZE(4),
    KICK(5),
    BAN(6);

    private final int severity;

    WardenAction(int severity) {
        this.severity = severity;
    }

    public int getSeverity() {
        return this.severity;
    }

    public boolean isPunitive() {
        return this.severity >= COMMAND.severity;
    }

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
