package dev.drawethree.xwarden.api.flags;

import java.util.Locale;

public enum WardenAction {

    LOG(0),
    ALERT(1),
    COMMAND(2),
    FREEZE(3),
    KICK(4),
    BAN(5);

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
