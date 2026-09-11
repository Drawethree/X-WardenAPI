package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

/**
 * One tracked identity.
 *
 * @param firstHolder who held the item when it was first stamped, or {@code null} when unknown -
 *                    the copy in their hands is the one kept when a duplicate is quarantined
 * @param status      {@link #STATUS_OK}, {@link #STATUS_DUPLICATE} or {@link #STATUS_TEMPLATE}
 */
public record FingerprintRecord(String uid,
                                String itemClass,
                                long firstSeen,
                                long lastSeen,
                                UUID lastHolder,
                                String lastLocation,
                                String status,
                                UUID firstHolder) {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_DUPLICATE = "DUPLICATE";
    /**
     * The identity was seen in too many different hands to be a duplicate: it is a kit, shop or
     * crate template that was saved from a stamped item. Never quarantined.
     */
    public static final String STATUS_TEMPLATE = "TEMPLATE";

    /** The shape before the first holder was recorded; kept so anything built against it still links. */
    public FingerprintRecord(String uid, String itemClass, long firstSeen, long lastSeen,
                             UUID lastHolder, String lastLocation, String status) {
        this(uid, itemClass, firstSeen, lastSeen, lastHolder, lastLocation, status, null);
    }
}
