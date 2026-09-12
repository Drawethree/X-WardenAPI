package dev.drawethree.xwarden.api.flags;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The findings X-Warden has recorded, and marking them as dealt with.
 *
 * <p>A finding carries a confidence, the evidence behind it, and what X-Warden did about it. It
 * never carries a recommendation: a statistical check is not entitled to an opinion on somebody's
 * account, so guidance suggests investigation and a human decides.
 *
 * @since 1.0.0
 */
public interface XWardenFlagsAPI {

    /**
     * One page of findings matching the query, newest first, with the total the filter matched.
     *
     * @param query what to match
     * @return the page
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    FlagPage page(ViolationQuery query);

    /**
     * The same findings, counted per player and per check rather than returned one row at a time.
     *
     * <p>Prefer this for anything a person reads. The signal checks report every analysis window
     * while somebody is still doing the thing, which is what builds a violation level and is
     * deliberate - so one macro miner is hundreds of rows in an evening and fills any row limit on
     * their own. Grouping happens in the query for the same reason: grouping rows after they came
     * back still lets the flooder push everybody else off the end of the list.
     *
     * @param query what to match; its page applies to groups, not rows
     * @return the groups, highest confidence first, then newest
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<FlagGroup> groups(ViolationQuery query);

    /**
     * One finding by its id.
     *
     * @param id the row id
     * @return the finding, or empty when there is none
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<Violation> byId(long id);

    /**
     * Reads several findings at once, so drawing a page of groups costs one round trip.
     *
     * @param ids the row ids
     * @return the findings that exist, in no particular order
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Violation> byIds(Collection<Long> ids);

    /**
     * The most recent findings, from memory rather than from the database.
     *
     * <p>Bounded and cleared on restart. For anything that has to be complete, use {@link #page}.
     *
     * @param limit the most to return
     * @return the findings, newest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<Violation> recent(int limit);

    /**
     * How many findings nobody has marked handled.
     *
     * @return the count
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countOpen();

    /**
     * How many open findings one player has.
     *
     * @param player the player
     * @return the count
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countOpenFor(UUID player);

    /**
     * How many open findings were recorded since a moment.
     *
     * @param since the moment, as epoch milliseconds
     * @return the count
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countOpenSince(long since);

    /**
     * How many findings this player has, per module, resolved ones included.
     *
     * @param player the player
     * @return module id to count
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Map<String, Integer> countsByModule(UUID player);

    /**
     * What was found since this moment, summarised for a digest.
     *
     * @param since the moment, as epoch milliseconds
     * @return the summary
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    ViolationSummary summarise(long since);

    /**
     * Marks one finding as dealt with, so it leaves the inbox.
     *
     * @param id    the finding
     * @param actor who did it, for the record
     * @return {@code false} when it was already resolved, or does not exist
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    boolean resolve(long id, String actor);

    /**
     * Marks every open finding of one check on one player as dealt with, so an ongoing problem is
     * one action rather than one per repeat.
     *
     * @param player     the player
     * @param playerName their name, for the record
     * @param checkId    the check
     * @param scope      what the finding was about - a currency, for the economy checks - or
     *                   {@code null}
     * @param latestId   the newest finding in the group, which is the one the resolved event names.
     *                   {@link FlagGroup#latestId()} carries it
     * @param actor      who did it, for the record
     * @return how many were resolved
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int resolveGroup(UUID player, String playerName, String checkId, String scope, long latestId,
                     String actor);

    /**
     * Marks every open finding on one player as dealt with.
     *
     * @param player     the player
     * @param playerName their name, for the record
     * @param actor      who did it, for the record
     * @return how many were resolved
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int resolveAll(UUID player, String playerName, String actor);

    /**
     * The guidance configured for a check: what it means, and what a false positive on it looks
     * like. Always something - a check with nothing written for it falls back to the general text.
     *
     * @param checkId the check
     * @return the lines, raw MiniMessage
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<String> guidanceFor(String checkId);
}
