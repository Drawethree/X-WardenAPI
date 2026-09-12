package dev.drawethree.xwarden.api.network;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.List;
import java.util.UUID;

/**
 * Accounts that look like the same hands.
 *
 * <p><strong>There is no action here, and there will not be one.</strong> Every signal this module
 * reads has an innocent explanation: a shared connection is a household or a school, a VPN defeats
 * it entirely, and two friends who play together join together and pay each other. There is no
 * responsible automatic punishment for alt correlation, so the code to do it does not exist - not
 * as a disabled option, not behind a config key. A correlation is something for a human to look at,
 * and nothing else.
 *
 * <p>Addresses are never stored. What is stored is a hash of one under a salt generated on first
 * start that never leaves the server.
 *
 * @since 1.0.0
 */
public interface XWardenNetworkAPI {

    /**
     * Accounts linked to this one at or above a confidence.
     *
     * @param player        the player
     * @param minConfidence the floor, 0 - 100
     * @return the links, strongest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<AccountLink> linksFor(UUID player, int minConfidence);

    /**
     * The strongest links on the server.
     *
     * @param limit         the most to return
     * @param minConfidence the floor, 0 - 100
     * @return the links, strongest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<AccountLink> topLinks(int limit, int minConfidence);

    /**
     * What the last analysis produced, from memory.
     *
     * @return the links; empty before the first analysis or when the network module is not running
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<AccountLink> lastComputed();

    /**
     * One player's visits, newest first.
     *
     * @param player the player
     * @param limit  the most to return
     * @return the sessions, newest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<SessionRecord> sessionsFor(UUID player, int limit);

    /**
     * Every visit that began since a moment.
     *
     * @param from the moment, as epoch milliseconds
     * @return the sessions
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<SessionRecord> sessionsSince(long from);

    /**
     * Re-runs the correlation now rather than waiting for the next scheduled pass.
     *
     * <p>Runs off the server thread; {@link #lastComputed()} answers with the result once it
     * finishes.
     *
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void analyseNow();
}
