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
 */
public final class WardenPunishmentEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Punishment punishment;
    private final boolean lifted;

    public WardenPunishmentEvent(Punishment punishment, boolean lifted) {
        this.punishment = punishment;
        this.lifted = lifted;
    }

    public Punishment getPunishment() {
        return this.punishment;
    }

    /** True when this announces the punishment being lifted rather than applied. */
    public boolean isLifted() {
        return this.lifted;
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
