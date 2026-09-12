package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

/**
 * One tracked identity.
 *
 * @param uid          the identity, as stamped into the item
 * @param itemClass    the class it was stamped as, such as {@code PICKAXE} or {@code CUSTOM}
 * @param firstSeen    when it was first recorded, as epoch milliseconds
 * @param lastSeen     when it was last seen
 * @param lastHolder   who last held it, or {@code null} for a container with no owner
 * @param lastLocation where it was last seen, in words
 * @param firstHolder who held the item when it was first stamped, or {@code null} when unknown -
 *                    the copy in their hands is the one kept when a duplicate is quarantined
 * @param status      {@link #STATUS_OK}, {@link #STATUS_DUPLICATE} or {@link #STATUS_TEMPLATE}
 * @since 1.0.0
 */
public record FingerprintRecord(String uid,
                                String itemClass,
                                long firstSeen,
                                long lastSeen,
                                UUID lastHolder,
                                String lastLocation,
                                String status,
                                UUID firstHolder) {

    /**
     * Seen in one place, as it should be.
     *
     * @since 1.0.0
     */
    public static final String STATUS_OK = "OK";

    /**
     * Seen in two places at once.
     *
     * @since 1.0.0
     */
    public static final String STATUS_DUPLICATE = "DUPLICATE";

    /**
     * The identity was seen in too many different hands to be a duplicate: it is a kit, shop or
     * crate template that was saved from a stamped item. Once recognised, no copy of it is taken.
     *
     * @since 1.1.0
     */
    public static final String STATUS_TEMPLATE = "TEMPLATE";

    /**
     * The shape before the first holder was recorded; kept so anything built against it still
     * links.
     *
     * @param uid          the identity
     * @param itemClass    the class
     * @param firstSeen    when first recorded
     * @param lastSeen     when last seen
     * @param lastHolder   who last held it, or {@code null}
     * @param lastLocation where, in words
     * @param status       the status
     * @since 1.0.0
     */
    public FingerprintRecord(String uid, String itemClass, long firstSeen, long lastSeen,
                             UUID lastHolder, String lastLocation, String status) {
        this(uid, itemClass, firstSeen, lastSeen, lastHolder, lastLocation, status, null);
    }
}
