package dev.drawethree.xwarden.api.event;

import dev.drawethree.xwarden.api.flags.EvidenceSnapshot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fired when a check has decided to report a player, before anything is recorded or acted on.
 *
 * <p>Cancelling discards the finding entirely: no row in the flag list, no violation level, and no
 * action. It is the same outcome as the player holding a bypass permission, which is what it is for
 * - a plugin that knows something Warden cannot, such as an event world, a staff build session, or
 * a player your own systems have already cleared.
 *
 * <p><b>This event is often fired off the main thread.</b> Analysis runs asynchronously, so a
 * handler must not touch the Bukkit API without hopping back to the server thread. Check
 * {@link #isAsynchronous()} if you need to know.
 *
 * <pre>{@code
 * @EventHandler
 * public void onWardenFlag(WardenViolationEvent event) {
 *     if (myEventWorld.contains(event.getPlayer())) {
 *         event.setCancelled(true);
 *     }
 * }
 * }</pre>
 */
public final class WardenViolationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final UUID player;
    private final String playerName;
    private final String moduleId;
    private final String checkId;
    private final String scope;
    private final int confidence;
    private final EvidenceSnapshot evidence;

    private boolean cancelled;

    public WardenViolationEvent(boolean async, UUID player, String playerName, String moduleId,
                                String checkId, String scope, int confidence,
                                EvidenceSnapshot evidence) {
        super(async);
        this.player = player;
        this.playerName = playerName;
        this.moduleId = moduleId;
        this.checkId = checkId;
        this.scope = scope;
        this.confidence = confidence;
        this.evidence = evidence;
    }

    public UUID getPlayer() {
        return this.player;
    }

    /** The name Warden knows them by, which may be a uuid fragment for somebody it never saw join. */
    public String getPlayerName() {
        return this.playerName;
    }

    public String getModuleId() {
        return this.moduleId;
    }

    public String getCheckId() {
        return this.checkId;
    }

    /** What the finding was narrowed to - a currency, for the economy checks - or {@code null}. */
    public String getScope() {
        return this.scope;
    }

    /** How sure the check is, 0 to 100. Not how serious it is. */
    public int getConfidence() {
        return this.confidence;
    }

    /** The measurements behind the finding. Never modify it. */
    public EvidenceSnapshot getEvidence() {
        return this.evidence;
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
