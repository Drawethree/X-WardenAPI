package dev.drawethree.xwarden.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fired after a staff member marks one finding as handled, so it has left the flag list.
 *
 * <p>Not cancellable: by the time this is fired the change is already stored. It is here so a
 * moderation panel or a Discord bot can close its own copy of the case without polling.
 *
 * <p><b>May be fired off the main thread</b>, because resolving writes to the database.
 *
 * @since 1.0.0
 */
public final class WardenFlagResolvedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long flagId;
    private final UUID player;
    private final String resolvedBy;
    private final long resolvedAt;

    /**
     * Creates the event. X-Warden fires it; a plugin only listens.
     *
     * @param async      whether it is being fired off the server thread
     * @param flagId     the finding
     * @param player     who it was about, or {@code null} for a bug notice
     * @param resolvedBy who marked it handled
     * @param resolvedAt when, as epoch milliseconds
     * @since 1.0.0
     */
    public WardenFlagResolvedEvent(boolean async, long flagId, UUID player, String resolvedBy,
                                   long resolvedAt) {
        super(async);
        this.flagId = flagId;
        this.player = player;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
    }

    /**
     * The finding that was marked handled.
     *
     * @return its id
     * @since 1.0.0
     */
    public long getFlagId() {
        return this.flagId;
    }

    /**
     * Who the finding was about.
     *
     * @return the player, or {@code null} when it was a bug notice rather than a player
     * @since 1.0.0
     */
    public UUID getPlayer() {
        return this.player;
    }

    /**
     * Who marked it handled.
     *
     * @return the staff member as {@code Name (uuid)}, the console, or {@code Warden (reason)}
     *         when X-Warden withdrew its own finding
     * @since 1.0.0
     */
    public String getResolvedBy() {
        return this.resolvedBy;
    }

    /**
     * When it was marked handled.
     *
     * @return epoch milliseconds
     * @since 1.0.0
     */
    public long getResolvedAt() {
        return this.resolvedAt;
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
     * @since 1.0.0
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
