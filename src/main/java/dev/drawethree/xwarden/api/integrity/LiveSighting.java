package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

/**
 * Where an identity was last seen, from the live index rather than a sweep.
 *
 * @param holder     the player holding it, or {@code null} for a block, the ground, a frame or a
 *                   registered container
 * @param place      where, in words
 * @param verifiable whether X-Warden can look at that place right now (an offline player's
 *                   inventory and an unloaded chunk are not)
 */
public record LiveSighting(String uid,
                           String itemClass,
                           UUID holder,
                           String holderName,
                           String place,
                           long seenAt,
                           boolean verifiable) {
}
