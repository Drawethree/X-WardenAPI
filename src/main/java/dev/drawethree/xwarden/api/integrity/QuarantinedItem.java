package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

/**
 * An item X-Warden took out of play.
 *
 * @param id         the row id, which {@code /xwarden quarantine restore} takes
 * @param uid        the identity it carried, or {@code null} for contraband with none
 * @param player     whose inventory it was in, or {@code null} when it sat in a block, on the
 *                   ground or in an item frame
 * @param playerName that player's name, or a description of the place
 * @param reason     {@link #REASON_DUPLICATE} or {@link #REASON_CONTRABAND}
 * @param takenFrom  where it was, in words
 * @param material   the Bukkit material name
 * @param displayName the item's custom name with formatting removed, or {@code null}
 * @param amount     the stack size
 * @param violationId the finding that took it, or {@code 0}
 * @param takenAt    when, as epoch milliseconds
 * @param restoredAt {@code 0} until a staff member gives it back
 * @param restoredBy who gave it back, or {@code null}
 * @since 1.1.0
 */
public record QuarantinedItem(long id,
                              String uid,
                              UUID player,
                              String playerName,
                              String reason,
                              String takenFrom,
                              String material,
                              String displayName,
                              int amount,
                              long violationId,
                              long takenAt,
                              long restoredAt,
                              String restoredBy) {

    /**
     * Taken because the same identity existed somewhere else.
     *
     * @since 1.1.0
     */
    public static final String REASON_DUPLICATE = "duplicate";

    /**
     * Taken because the item is one only an operator could have created.
     *
     * @since 1.1.0
     */
    public static final String REASON_CONTRABAND = "contraband";

    /**
     * Whether a staff member has handed it back.
     *
     * @return whether {@link #restoredAt()} is set
     * @since 1.1.0
     */
    public boolean isRestored() {
        return this.restoredAt > 0L;
    }
}
