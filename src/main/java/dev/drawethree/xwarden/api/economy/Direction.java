package dev.drawethree.xwarden.api.economy;

/**
 * Which way money moved in one ledger entry.
 *
 * @since 1.0.0
 */
public enum Direction {
    /** The player's balance went up. */
    CREDIT,
    /** The player's balance went down. */
    DEBIT
}
