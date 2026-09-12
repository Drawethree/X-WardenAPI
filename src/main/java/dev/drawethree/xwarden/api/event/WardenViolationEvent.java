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
 *
 * @since 1.0.0
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

    /**
     * Creates the event. X-Warden fires it; a plugin only listens.
     *
     * @param async      whether it is being fired off the server thread
     * @param player     who the finding is about
     * @param playerName their name
     * @param moduleId   the module
     * @param checkId    the check
     * @param scope      what it was narrowed to, or {@code null}
     * @param confidence 0 - 100
     * @param evidence   the measurements behind it
     * @since 1.0.0
     */
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

    /**
     * Who the finding is about.
     *
     * @return the player
     * @since 1.0.0
     */
    public UUID getPlayer() {
        return this.player;
    }

    /**
     * The name Warden knows them by.
     *
     * @return the name, which may be the one on disk for somebody Warden never saw join
     * @since 1.0.0
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * The module the check belongs to.
     *
     * @return the module id
     * @since 1.0.0
     */
    public String getModuleId() {
        return this.moduleId;
    }

    /**
     * The check that fired.
     *
     * @return the check id, as spelled in {@code warden.yml}
     * @since 1.0.0
     */
    public String getCheckId() {
        return this.checkId;
    }

    /**
     * What the finding was narrowed to.
     *
     * @return a currency, for the economy checks, or {@code null}
     * @since 1.0.0
     */
    public String getScope() {
        return this.scope;
    }

    /**
     * How sure the check is. Not how serious it is.
     *
     * @return 0 to 100
     * @since 1.0.0
     */
    public int getConfidence() {
        return this.confidence;
    }

    /**
     * The measurements behind the finding.
     *
     * @return the evidence, which is immutable
     * @since 1.0.0
     */
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
