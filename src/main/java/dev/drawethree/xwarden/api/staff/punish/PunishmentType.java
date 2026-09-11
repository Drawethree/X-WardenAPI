package dev.drawethree.xwarden.api.staff.punish;

import java.util.Locale;

/** What a punishment does to the player. */
public enum PunishmentType {

    BAN(true, true),
    IP_BAN(true, true),
    MUTE(true, true),
    IP_MUTE(true, true),
    WARN(false, false),
    KICK(false, false);

    private final boolean timed;
    private final boolean liftable;

    PunishmentType(boolean timed, boolean liftable) {
        this.timed = timed;
        this.liftable = liftable;
    }

    /** Whether this type carries a duration. A warning and a kick are over the moment they happen. */
    public boolean isTimed() {
        return this.timed;
    }

    /** Whether staff can lift it early. */
    public boolean isLiftable() {
        return this.liftable;
    }

    /** Whether it needs the player's address, which is only known while they are online. */
    public boolean needsAddress() {
        return this == IP_BAN || this == IP_MUTE;
    }

    /** The key this type uses in configuration: {@code ip-ban}, {@code mute}, ... */
    public String key() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    public static PunishmentType parse(String raw) {
        if (raw == null) {
            return null;
        }
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException unknown) {
            return null;
        }
    }
}
