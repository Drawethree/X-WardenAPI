package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

/**
 * Where one identity was seen during one sweep.
 *
 * @param uid        the identity
 * @param scanId     which sweep
 * @param holder     the player whose storage it was in, or {@code null} for a container with no
 *                   owner
 * @param holderName that holder's name
 * @param container  {@code inventory}, {@code enderchest}, or a registered container's id
 * @param slot       the slot, or {@code -1} where the container has no slots
 * @since 1.0.0
 */
public record Sighting(String uid,
                       long scanId,
                       UUID holder,
                       String holderName,
                       String container,
                       int slot) {
}
