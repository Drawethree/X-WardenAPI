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
 * @param moduleId {@code economy}, {@code integrity}, {@code automation}, {@code network}, or
 *                 {@code null} for all of them
 * @param checkId  a single check, or {@code null} for all of them
 * @param scope    what the finding was about - a currency, for the economy checks - or {@code null}
 *                 for any. Pass {@code ""} to match only findings that have no scope
 * @param openOnly whether to skip findings a staff member has already marked as handled
 * @param since    the earliest moment to include, as epoch milliseconds; {@code 0} for no floor
 * @param until    the latest moment to include, as epoch milliseconds; {@link Long#MAX_VALUE} for
 *                 no ceiling
 * @param offset   how many rows to skip, for paging
 * @param limit    how many rows to return at most
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

    public ViolationQuery {
        if (limit < 0) {
            throw new IllegalArgumentException("limit cannot be negative");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset cannot be negative");
        }
    }

    public static ViolationQuery all(int limit) {
        return new ViolationQuery(null, null, null, null, false, 0L, Long.MAX_VALUE, 0, limit);
    }

    public static ViolationQuery forPlayer(UUID player, int limit) {
        return all(limit).ofPlayer(player);
    }

    public static ViolationQuery forModule(String moduleId, int limit) {
        return all(limit).inModule(moduleId);
    }

    public ViolationQuery ofPlayer(UUID player) {
        return new ViolationQuery(player, this.moduleId, this.checkId, this.scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    public ViolationQuery inModule(String moduleId) {
        return new ViolationQuery(this.player, moduleId, this.checkId, this.scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    public ViolationQuery ofCheck(String checkId) {
        return new ViolationQuery(this.player, this.moduleId, checkId, this.scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    public ViolationQuery inScope(String scope) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, scope, this.openOnly,
                this.since, this.until, this.offset, this.limit);
    }

    public ViolationQuery openOnly(boolean only) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, this.scope, only,
                this.since, this.until, this.offset, this.limit);
    }

    public ViolationQuery between(long since, long until) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, this.scope, this.openOnly,
                since, until, this.offset, this.limit);
    }

    public ViolationQuery page(int offset, int limit) {
        return new ViolationQuery(this.player, this.moduleId, this.checkId, this.scope, this.openOnly,
                this.since, this.until, offset, limit);
    }

    public boolean hasTimeBounds() {
        return this.since > 0L || this.until < Long.MAX_VALUE;
    }
}
