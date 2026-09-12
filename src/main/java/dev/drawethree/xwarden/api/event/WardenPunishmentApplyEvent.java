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
 *
 * @since 1.1.0
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

    /**
     * Creates the event. X-Warden fires it; a plugin only listens.
     *
     * @param player     who is about to be punished
     * @param playerName their name
     * @param preset     the preset id
     * @param step       the rung of its ladder that applies
     * @param reason     what the player will be told
     * @param staff      who asked for it
     * @param findingId  the finding it answers, or {@code 0}
     * @since 1.1.0
     */
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

    /**
     * Who is about to be punished.
     *
     * @return the player
     * @since 1.1.0
     */
    public UUID getPlayer() {
        return this.player;
    }

    /**
     * Their name, as X-Warden knows it.
     *
     * @return the name
     * @since 1.1.0
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * The preset being applied.
     *
     * @return its id in {@code warden-punishments.yml}
     * @since 1.1.0
     */
    public String getPreset() {
        return this.preset;
    }

    /**
     * The rung of the ladder that applies this time.
     *
     * @return the step
     * @since 1.1.0
     */
    public PunishmentStep getStep() {
        return this.step;
    }

    /**
     * What the player will be told.
     *
     * @return the reason
     * @since 1.1.0
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * Who asked for it.
     *
     * @return a staff member as {@code Name (uuid)}, a plugin name, or the check that fired
     * @since 1.1.0
     */
    public String getStaff() {
        return this.staff;
    }

    /**
     * The finding it answers.
     *
     * @return its id, or {@code 0} when staff issued the punishment directly
     * @since 1.1.0
     */
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

    /**
     * Bukkit's handler list for this event.
     *
     * @return the handler list
     * @since 1.1.0
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
