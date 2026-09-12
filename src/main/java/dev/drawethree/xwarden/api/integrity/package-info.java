/**
 * Duplicated items, proven rather than suspected: identities, sightings, quarantine and the
 * container sources a storage plugin registers to take part.
 *
 * <p>Anything that touches an {@code ItemStack} must be called on the server thread; the stored
 * records are read off it; the live index is safe from anywhere.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.integrity;
