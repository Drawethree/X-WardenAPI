package dev.drawethree.xwarden.api.client;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The client each player joined with, and what X-Warden made of it.
 *
 * <p>Three things are read: the brand the client announces, the plugin channels it registers, and
 * - on Java clients only - a probe that asks the client to resolve a key or a translation that only
 * a particular mod ships. The first two are self-declarations and can be faked either way; the
 * probe is the client answering for itself.
 *
 * @since 1.1.0
 */
public interface XWardenClientAPI {

    /**
     * The verdict on an online player, once the join sequence has concluded.
     *
     * @param player the player
     * @return the profile, or empty while the sequence is still running, when the player is
     *         offline, or when the client module is not running
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<ClientProfile> profile(UUID player);

    /**
     * Every online player with a concluded verdict.
     *
     * @return the profiles; empty when the client module is not running
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.ANY)
    List<ClientProfile> online();

    /**
     * Runs the join sequence again for an online player, ignoring the cooldown.
     *
     * @param player the player
     * @return {@code false} when the player is offline or the client module is not running
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean recheck(UUID player);
}
