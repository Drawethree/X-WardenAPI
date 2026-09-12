package dev.drawethree.xwarden.api.event;

import dev.drawethree.xwarden.api.staff.punish.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired on the server thread after a punishment has been recorded and applied, or lifted.
 *
 * <p>Not cancellable: by now it has happened. For a Discord bot, a panel, or a plugin keeping its
 * own ledger.
 *
 * @since 1.1.0
 */
public final class WardenPunishmentEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Punishment punishment;
    private final boolean lifted;

    /**
     * Creates the event. X-Warden fires it; a plugin only listens.
     *
     * @param punishment the record, as stored
     * @param lifted     whether this announces it being lifted rather than applied
     * @since 1.1.0
     */
    public WardenPunishmentEvent(Punishment punishment, boolean lifted) {
        this.punishment = punishment;
        this.lifted = lifted;
    }

    /**
     * The punishment, as recorded.
     *
     * @return the record
     * @since 1.1.0
     */
    public Punishment getPunishment() {
        return this.punishment;
    }

    /**
     * Whether this announces the punishment being lifted rather than applied.
     *
     * @return {@code true} for a lift
     * @since 1.1.0
     */
    public boolean isLifted() {
        return this.lifted;
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
