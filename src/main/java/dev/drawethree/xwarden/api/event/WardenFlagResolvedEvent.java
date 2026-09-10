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
 */
public final class WardenFlagResolvedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long flagId;
    private final UUID player;
    private final String resolvedBy;
    private final long resolvedAt;

    public WardenFlagResolvedEvent(boolean async, long flagId, UUID player, String resolvedBy,
                                   long resolvedAt) {
        super(async);
        this.flagId = flagId;
        this.player = player;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = resolvedAt;
    }

    public long getFlagId() {
        return this.flagId;
    }

    /** Who the finding was about, or {@code null} when it was a bug notice rather than a player. */
    public UUID getPlayer() {
        return this.player;
    }

    /** The staff member, as {@code Name (uuid)}, or the console. */
    public String getResolvedBy() {
        return this.resolvedBy;
    }

    public long getResolvedAt() {
        return this.resolvedAt;
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
