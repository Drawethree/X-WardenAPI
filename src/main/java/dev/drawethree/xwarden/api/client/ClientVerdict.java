package dev.drawethree.xwarden.api.client;

/**
 * What X-Warden concluded about the client a player joined with.
 *
 * @since 1.1.0
 */
public enum ClientVerdict {
    /** Nothing seen yet, or nothing the signatures recognise. */
    UNKNOWN,
    /** A client on the allow list: vanilla, or a launcher known to be harmless. */
    ALLOWED,
    /** A Bedrock player through Geyser. Never probed. */
    BEDROCK,
    /** A modded client with no cheat signature: Fabric, Forge, a mod that announces itself. */
    MODDED,
    /** A client that matched a cheat signature. */
    CHEAT;

    /**
     * The stronger of two verdicts, in the order they are declared.
     *
     * @param other the other verdict, or {@code null}
     * @return whichever is declared later, so {@code CHEAT} always wins
     * @since 1.1.0
     */
    public ClientVerdict or(ClientVerdict other) {
        return other != null && other.ordinal() > ordinal() ? other : this;
    }
}
