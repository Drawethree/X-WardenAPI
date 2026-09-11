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
 */
public final class WardenQuarantineEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final QuarantinedItem item;
    private final boolean restored;

    public WardenQuarantineEvent(QuarantinedItem item, boolean restored) {
        this.item = item;
        this.restored = restored;
    }

    public QuarantinedItem getItem() {
        return this.item;
    }

    public boolean isRestored() {
        return this.restored;
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
