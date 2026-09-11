package dev.drawethree.xwarden.api.staff.punish;

import java.util.List;

/**
 * A named ladder of punishments, so staff apply the same escalation every time.
 *
 * <p>The first application of a preset to a player takes the first step, the next the second, and
 * so on; past the last step the last one repeats. What counts as a prior application - the same
 * preset, or the same type of punishment - and how far back is configured, not decided here.
 *
 * @param id       the key in {@code warden-punishments.yml}
 * @param name     what staff see, raw MiniMessage
 * @param reason   what the player and the ban plugin see
 * @param material the icon in the menu, as a Bukkit material name
 * @param lore     extra menu lines, raw MiniMessage
 * @param ladder   at least one step
 */
public record PunishmentPreset(String id,
                               String name,
                               String reason,
                               String material,
                               List<String> lore,
                               List<PunishmentStep> ladder) {

    /** The step that applies after this many prior applications. */
    public PunishmentStep stepFor(int prior) {
        int index = Math.max(0, Math.min(this.ladder.size() - 1, prior));
        return this.ladder.get(index);
    }
}
