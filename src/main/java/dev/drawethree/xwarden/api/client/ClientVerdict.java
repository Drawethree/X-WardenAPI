package dev.drawethree.xwarden.api.client;

/** What X-Warden concluded about the client a player joined with. */
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

    /** The stronger of two verdicts, in the order they are declared. */
    public ClientVerdict or(ClientVerdict other) {
        return other != null && other.ordinal() > ordinal() ? other : this;
    }
}
