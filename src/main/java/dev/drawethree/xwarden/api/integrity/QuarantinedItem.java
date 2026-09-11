package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

/**
 * An item X-Warden took out of play.
 *
 * @param uid        the identity it carried, or {@code null} for contraband with none
 * @param player     whose inventory it was in, or {@code null} when it sat in a block, on the
 *                   ground or in an item frame
 * @param reason     {@link #REASON_DUPLICATE} or {@link #REASON_CONTRABAND}
 * @param takenFrom  where it was, in words
 * @param restoredAt {@code 0} until a staff member gives it back
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

    public static final String REASON_DUPLICATE = "duplicate";
    public static final String REASON_CONTRABAND = "contraband";

    public boolean isRestored() {
        return this.restoredAt > 0L;
    }
}
