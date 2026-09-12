package dev.drawethree.xwarden.api.event;

import dev.drawethree.xwarden.api.flags.Violation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired once a finding has been recorded, with everything about it.
 *
 * <p>Not the same event as {@link WardenViolationEvent}, and the difference matters if you are
 * showing findings to a person. That one fires <em>before</em> anything is written, so a plugin can
 * veto it - which means it has no id, no violation level and no record of what X-Warden decided to
 * do, because none of those exist yet. This one fires afterwards and carries the finished
 * {@link Violation}: an id to link to, the level it pushed the player to, and the action taken.
 *
 * <p>Not cancellable. By the time it fires the finding is stored and any action has been taken.
 *
 * <p><b>Usually fired off the main thread.</b> Most checks run on an analysis task rather than on
 * the server thread, so handlers must not touch the server directly, and must not block: this is
 * fired from the path that detects cheating, and a slow listener slows detection down. Hand the
 * work to a queue of your own.
 *
 * @since 1.0.0
 */
public final class WardenViolationRecordedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Violation violation;

    /**
     * Creates the event. X-Warden fires it; a plugin only listens.
     *
     * @param async     whether it is being fired off the server thread
     * @param violation the finding, as stored
     * @since 1.0.0
     */
    public WardenViolationRecordedEvent(boolean async, Violation violation) {
        super(async);
        this.violation = violation;
    }

    /**
     * The finding, as stored: with its id, the level it pushed the player to, and the action taken.
     *
     * @return the violation
     * @since 1.0.0
     */
    @NotNull
    public Violation getViolation() {
        return this.violation;
    }

    /**
     * Whether this is a defect report rather than something a player did.
     *
     * <p>A malformed amount from a broken plugin is a bug in whatever produced it. It reaches staff
     * as a notice and never touches anybody's violation level, so it should not be shown as an
     * accusation.
     *
     * @return whether this is a bug notice
     * @since 1.0.0
     */
    public boolean isBugNotice() {
        return this.violation.isBugNotice();
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
