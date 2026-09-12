package dev.drawethree.xwarden.api.integrity;

/**
 * What came of giving a quarantined item back.
 *
 * @since 1.1.0
 */
public enum RestoreOutcome {
    /** The item is back in the player's inventory, with a fresh identity. */
    RESTORED,
    /** No quarantined item has that id, or the integrity module is not running. */
    NOT_FOUND,
    /** It was handed back already. */
    ALREADY_RESTORED,
    /** The item has to be handed to somebody who is online; there is no offline inventory. */
    PLAYER_OFFLINE,
    /** The stored copy could not be read back into an item. It stays in quarantine. */
    UNREADABLE
}
