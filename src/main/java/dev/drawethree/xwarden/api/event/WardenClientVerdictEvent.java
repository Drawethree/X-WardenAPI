package dev.drawethree.xwarden.api.event;

import dev.drawethree.xwarden.api.client.ClientProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired on the server thread when the join sequence has concluded what client a player uses.
 *
 * <p>Not cancellable, and fired before any action: a {@code CHEAT} verdict then goes through the
 * ordinary finding path, where {@link WardenViolationEvent} can still discard it.
 */
public final class WardenClientVerdictEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ClientProfile profile;

    public WardenClientVerdictEvent(ClientProfile profile) {
        this.profile = profile;
    }

    public ClientProfile getProfile() {
        return this.profile;
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
