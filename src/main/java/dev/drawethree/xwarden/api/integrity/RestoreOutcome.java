package dev.drawethree.xwarden.api.integrity;

/** What came of giving a quarantined item back. */
public enum RestoreOutcome {
    RESTORED,
    NOT_FOUND,
    ALREADY_RESTORED,
    /** The item has to be handed to somebody who is online; there is no offline inventory. */
    PLAYER_OFFLINE,
    /** The stored copy could not be read back into an item. It stays in quarantine. */
    UNREADABLE
}
