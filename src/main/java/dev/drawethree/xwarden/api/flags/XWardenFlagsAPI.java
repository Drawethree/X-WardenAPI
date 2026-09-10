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
 */
public interface XWardenFlagsAPI {

    /**
     * One page of findings matching the query, newest first, with the total the filter matched.
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
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<FlagGroup> groups(ViolationQuery query);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<Violation> byId(long id);

    /** Reads several findings at once, so drawing a page of groups costs one round trip. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Violation> byIds(Collection<Long> ids);

    /**
     * The most recent findings, from memory rather than from the database.
     *
     * <p>Bounded and cleared on restart. For anything that has to be complete, use {@link #page}.
     */
    @ThreadSafety(Requirement.ANY)
    List<Violation> recent(int limit);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countOpen();

    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countOpenFor(UUID player);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countOpenSince(long since);

    /** How many findings this player has, per module, resolved ones included. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Map<String, Integer> countsByModule(UUID player);

    /** What was found since this moment, summarised for a digest. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    ViolationSummary summarise(long since);

    /**
     * Marks one finding as dealt with, so it leaves the inbox.
     *
     * @param actor who did it, for the record
     * @return false when it was already resolved
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    boolean resolve(long id, String actor);

    /**
     * Marks every open finding of one check on one player as dealt with, so an ongoing problem is
     * one action rather than one per repeat.
     *
     * @param scope    what the finding was about - a currency, for the economy checks - or null
     * @param latestId the newest finding in the group, which is the one the resolved event names.
     *                 {@link FlagGroup#latestId()} carries it
     * @return how many were resolved
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int resolveGroup(UUID player, String playerName, String checkId, String scope, long latestId,
                     String actor);

    /** Marks every open finding on one player as dealt with. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int resolveAll(UUID player, String playerName, String actor);

    /**
     * The guidance configured for a check: what it means, and what a false positive on it looks
     * like. Always something - a check with nothing written for it falls back to the general text.
     */
    @ThreadSafety(Requirement.ANY)
    List<String> guidanceFor(String checkId);
}
