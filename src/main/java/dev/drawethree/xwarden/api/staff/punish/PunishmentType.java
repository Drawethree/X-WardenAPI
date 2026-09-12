package dev.drawethree.xwarden.api.staff.punish;

import java.util.Locale;
import java.util.Optional;

/**
 * What a punishment does to the player.
 *
 * @since 1.1.0
 */
public enum PunishmentType {

    /** Keeps the account off the server. */
    BAN(true, true),
    /** Keeps the address off the server. Needs the player online to read it. */
    IP_BAN(true, true),
    /** Stops the account chatting. */
    MUTE(true, true),
    /** Stops the address chatting. Needs the player online to read it. */
    IP_MUTE(true, true),
    /** Tells the player, and nothing more. Over the moment it happens. */
    WARN(false, false),
    /** Disconnects the player. Over the moment it happens. */
    KICK(false, false);

    private final boolean timed;
    private final boolean liftable;

    PunishmentType(boolean timed, boolean liftable) {
        this.timed = timed;
        this.liftable = liftable;
    }

    /**
     * Whether this type carries a duration. A warning and a kick are over the moment they happen.
     *
     * @return whether it is timed
     * @since 1.1.0
     */
    public boolean isTimed() {
        return this.timed;
    }

    /**
     * Whether staff can lift it early.
     *
     * @return whether it is liftable
     * @since 1.1.0
     */
    public boolean isLiftable() {
        return this.liftable;
    }

    /**
     * Whether it needs the player's address, which is only known while they are online.
     *
     * @return {@code true} for {@link #IP_BAN} and {@link #IP_MUTE}
     * @since 1.1.0
     */
    public boolean needsAddress() {
        return this == IP_BAN || this == IP_MUTE;
    }

    /**
     * The key this type uses in configuration.
     *
     * @return {@code ban}, {@code ip-ban}, {@code mute}, {@code ip-mute}, {@code warn} or
     *         {@code kick}
     * @since 1.1.0
     */
    public String key() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }

    /**
     * Reads a type from configuration text, accepting either spelling.
     *
     * @param raw {@code ip-ban} or {@code IP_BAN}, in any case, or {@code null}
     * @return the type, or empty for text that names none
     * @since 1.1.0
     */
    public static Optional<PunishmentType> parse(String raw) {
        if (raw == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(raw.trim().toUpperCase(Locale.ROOT).replace('-', '_')));
        } catch (IllegalArgumentException unknown) {
            return Optional.empty();
        }
    }
}
