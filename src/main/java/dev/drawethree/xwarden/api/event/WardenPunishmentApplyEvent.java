package dev.drawethree.xwarden.api.event;

import dev.drawethree.xwarden.api.staff.punish.PunishmentStep;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fired on the server thread before a punishment is recorded or applied.
 *
 * <p>Cancelling stops it entirely: nothing is written and no command runs. For a plugin that knows
 * the player is exempt, or that wants to route the punishment through a system of its own.
 */
public final class WardenPunishmentApplyEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID player;
    private final String playerName;
    private final String preset;
    private final PunishmentStep step;
    private final String reason;
    private final String staff;
    private final long findingId;

    private boolean cancelled;

    public WardenPunishmentApplyEvent(UUID player, String playerName, String preset,
                                      PunishmentStep step, String reason, String staff,
                                      long findingId) {
        this.player = player;
        this.playerName = playerName;
        this.preset = preset;
        this.step = step;
        this.reason = reason;
        this.staff = staff;
        this.findingId = findingId;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getPreset() {
        return this.preset;
    }

    public PunishmentStep getStep() {
        return this.step;
    }

    public String getReason() {
        return this.reason;
    }

    /** Who asked for it, as {@code Name (uuid)}, a plugin name, or the check that fired. */
    public String getStaff() {
        return this.staff;
    }

    /** The finding it answers, or {@code 0}. */
    public long getFindingId() {
        return this.findingId;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
