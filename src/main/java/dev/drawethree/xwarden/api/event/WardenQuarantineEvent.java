package dev.drawethree.xwarden.api.event;

import dev.drawethree.xwarden.api.integrity.QuarantinedItem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired on the server thread after an item was taken out of play, or handed back.
 *
 * <p>Not cancellable. To stop an item being taken, cancel the {@link WardenViolationEvent} that
 * precedes it: a finding that is discarded quarantines nothing.
 *
 * @since 1.1.0
 */
public final class WardenQuarantineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final QuarantinedItem item;
    private final boolean restored;

    /**
     * Creates the event. X-Warden fires it; a plugin only listens.
     *
     * @param item     the item, as stored
     * @param restored whether this announces it being handed back rather than taken
     * @since 1.1.0
     */
    public WardenQuarantineEvent(QuarantinedItem item, boolean restored) {
        this.item = item;
        this.restored = restored;
    }

    /**
     * The item that was taken or handed back.
     *
     * @return the record
     * @since 1.1.0
     */
    public QuarantinedItem getItem() {
        return this.item;
    }

    /**
     * Whether this announces the item being handed back rather than taken.
     *
     * @return {@code true} for a restore
     * @since 1.1.0
     */
    public boolean isRestored() {
        return this.restored;
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
