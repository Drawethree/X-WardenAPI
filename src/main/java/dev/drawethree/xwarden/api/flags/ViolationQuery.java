package dev.drawethree.xwarden.api.flags;

import java.util.UUID;

/**
 * What to look for in the stored findings.
 * <p>
 * Every field narrows; leaving one at its default matches everything. The narrowing is applied in
 * SQL rather than after the rows come back, which matters more than it sounds: one player being
 * flagged every analysis window can fill any row limit on their own, so a filter applied afterwards
 * would leave every other player's findings outside the result set entirely.
 *
 * @param player   whose findings, or {@code null} for everybody's
 * @param moduleId {@code economy}, {@code integrity}, {@code automation}, {@code network},
 *                 {@code client}, or {@code null} for all of them
 * @param checkId  a single check, or {@code null} for all of them
 * @param scope    what the finding was about - a currency, for the economy checks - or {@code null}
 *                 for any. Pass {@code ""} to match only findings that have no scope
 * @param openOnly whether to skip findings a staff member has already marked as handled
 * @param since    the earliest moment to include, as epoch milliseconds; {@code 0} for no floor
 * @param until    the latest moment to include, as epoch milliseconds; {@link Long#MAX_VALUE} for
 *                 no ceiling
 * @param offset   how many rows to skip, for paging
 * @param limit    how many rows to return at most
 * @since 1.0.0
 */
public record ViolationQuery(UUID player,
                             String moduleId,
                             String checkId,
                             String scope,
                             boolean openOnly,
                             long since,
                             long until,
                             int offset,
                             int limit) {

    /**
     * Refuses a negative page.
     *
     * @param player   whose findings, or {@code null}
     * @param moduleId the module, or {@code null}
     * @param checkId  the check, or {@code null}
     * @param scope    the scope, {@code ""} for none, or {@code null} for any
     * @param openOnly whether to skip handled findings
     * @param since    the earliest moment, or {@code 0}
     * @param until    the latest moment, or {@link Long#MAX_VALUE}
     * @param offset   rows to skip
     * @param limit    rows to return at most
     * @throws IllegalArgumentException if {@code offset} or {@code limit} is negative
     */
    public ViolationQuery {
        if (limit < 0) {
            throw new IllegalArgumentException("limit cannot be negative");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
    }

    /**
     * Everything, newest first.
     *
     * @param limit how many rows at most
     * @return the query
     * @since 1.0.0
     */
    public static ViolationQuery all(int limit) {
        return new ViolationQuery(null, null, null, null, false, 0L, Long.MAX_VALUE, 0, limit);
    }

    /**
     * One player's findings, newest first.
     *
     * @param player the player
     * @param limit  how many rows at most
     * @return the query
     * @since 1.0.0
     */
    public static ViolationQuery forPlayer(UUID player, int limit) {
        return all(limit).ofPlayer(player);
    }

    /**
     * One module's findings, newest first.
     *
     * @param moduleId the module
     * @param limit    how many rows at most
     * @return the query
     * @since 1.0.0
     */
    public static ViolationQuery forModule(String moduleId, int limit) {
        return all(limit).inModule(moduleId);
    }

    /**
     * Narrows to one player.
     *
     * @param player the player, or {@code null} for everybody
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery ofPlayer(UUID player) {
        return new ViolationQuery(player, this.moduleId, this.checkId, this.scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    /**
     * Narrows to one module.
     *
     * @param moduleId the module, or {@code null} for all
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery inModule(String moduleId) {
        return new ViolationQuery(this.player, moduleId, this.checkId, this.scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    /**
     * Narrows to one check.
     *
     * @param checkId the check, or {@code null} for all
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery ofCheck(String checkId) {
        return new ViolationQuery(this.player, this.moduleId, checkId, this.scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    /**
     * Narrows to one scope.
     *
     * @param scope the scope, {@code ""} for findings with none, or {@code null} for any
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery inScope(String scope) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    /**
     * Whether to skip findings a staff member has marked handled.
     *
     * @param only {@code true} to skip them
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery openOnly(boolean only) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, this.scope, only,
                this.since, this.until, this.offset, this.limit);
    }

    /**
     * Narrows to a window of time.
     *
     * @param since the earliest moment, as epoch milliseconds, or {@code 0}
     * @param until the latest, or {@link Long#MAX_VALUE}
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery between(long since, long until) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, this.scope, this.openOnly,
                since, until, this.offset, this.limit);
    }

    /**
     * Picks a page.
     *
     * @param offset rows to skip
     * @param limit  rows to return at most
     * @return a new query
     * @since 1.0.0
     */
    public ViolationQuery page(int offset, int limit) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, this.scope, this.openOnly,
                this.since, this.until, offset, limit);
    }

    /**
     * Whether the query is narrowed in time at all.
     *
     * @return whether either bound is set
     * @since 1.0.0
     */
    public boolean hasTimeBounds() {
        return this.since > 0L || this.until < Long.MAX_VALUE;
    }
}
