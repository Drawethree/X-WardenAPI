package dev.drawethree.xwarden.api;

import java.util.List;
import java.util.UUID;

/**
 * One sighting of one tracked item: what it is, who has it, and where.
 *
 * <p>Build these with {@link XWardenAPI#describeStored} rather than by hand. Only Warden knows
 * which identity scheme an item uses, and it fills in what the item looks like at the same time,
 * on the server thread, so a finding can name the item rather than only its identifier.
 *
 * @param uid         the identity the item is recognised by
 * @param itemClass   the class it was fingerprinted as, such as {@code PICKAXE} or {@code CUSTOM}
 * @param holder      whose storage it was found in, or {@code null} for a container with no owner
 * @param holderName  that holder's name, as it should read to a member of staff
 * @param container   where it was found: {@code inventory}, {@code enderchest}, or your own id
 * @param slot        the slot it sat in, or {@code -1} where the container has no slots
 * @param amount      the size of the stack
 * @param material    the Bukkit material name, so a finding can say what was duplicated
 * @param displayName the item's custom name with formatting removed, or {@code null}
 * @param lore        its lore with formatting removed, which is where a prison pickaxe keeps the
 *                    enchants that tell two otherwise identical items apart
 */
public record HeldItem(String uid,
                       String itemClass,
                       UUID holder,
                       String holderName,
                       String container,
                       int slot,
                       int amount,
                       String material,
                       String displayName,
                       List<String> lore) {

    public HeldItem {
        lore = lore == null ? List.of() : List.copyOf(lore);
    }
}
