package dev.drawethree.xwarden.api.flags;

import java.util.UUID;

/**
 * One finding, as X-Warden recorded it.
 *
 * @param id             the row id, which is what alerts and menus link to
 * @param timestamp      when it was recorded, as epoch milliseconds
 * @param player         who it is about, or the nil UUID for a bug notice
 * @param playerName     their name at the time
 * @param moduleId       the module the check belongs to, or {@code core} for a bug notice
 * @param checkId        the check that fired
 * @param scope          what it was narrowed to - a currency, for the economy checks - or
 *                       {@code null}
 * @param confidence     how sure the check was, 0 - 100
 * @param violationLevel the level the player was at once this was counted, rounded down
 * @param action         what X-Warden did about it
 * @param evidence       the measurements behind it
 * @param resolution     who marked it handled and when, or {@code null} while it is open
 * @since 1.0.0
 */
public record Violation(long id,
                        long timestamp,
                        UUID player,
                        String playerName,
                        String moduleId,
                        String checkId,
                        String scope,
                        int confidence,
                        int violationLevel,
                        WardenAction action,
                        EvidenceSnapshot evidence,
                        Resolution resolution) {

    /**
     * An open finding.
     *
     * @param id             the row id
     * @param timestamp      when it was recorded
     * @param player         who it is about
     * @param playerName     their name
     * @param moduleId       the module
     * @param checkId        the check
     * @param scope          what it was narrowed to, or {@code null}
     * @param confidence     0 - 100
     * @param violationLevel the level once this was counted
     * @param action         what X-Warden did
     * @param evidence       the measurements
     * @since 1.0.0
     */
    public Violation(long id, long timestamp, UUID player, String playerName, String moduleId,
                     String checkId, String scope, int confidence, int violationLevel,
                     WardenAction action, EvidenceSnapshot evidence) {
        this(id, timestamp, player, playerName, moduleId, checkId, scope, confidence, violationLevel,
                action, evidence, null);
    }

    /**
     * Whether a staff member has marked this finding handled.
     *
     * @return whether {@link #resolution()} is set
     * @since 1.0.0
     */
    public boolean isResolved() {
        return this.resolution != null;
    }

    /**
     * The check id, narrowed to what the finding was about.
     *
     * <p>Violation levels are counted per scope, so a player laundering tokens does not accumulate a
     * level against the money they earn honestly.
     *
     * @return {@code check} or {@code check@scope}
     * @since 1.0.0
     */
    public String scopedCheckId() {
        return this.scope == null || this.scope.isEmpty() ? this.checkId : this.checkId + "@" + this.scope;
    }

    /**
     * The band the confidence falls in.
     *
     * @return the severity
     * @since 1.0.0
     */
    public Severity severity() {
        return Severity.of(this.confidence);
    }

    /**
     * Whether this finding deserves attention ahead of the rest.
     *
     * @return {@code true} for a critical confidence or a punitive action
     * @since 1.0.0
     */
    public boolean isHighSeverity() {
        return severity() == Severity.CRITICAL || this.action.isPunitive();
    }

    /**
     * Whether this is a defect report rather than something a player did.
     *
     * @return whether the module is {@code core}
     * @since 1.0.0
     */
    public boolean isBugNotice() {
        return "core".equals(this.moduleId);
    }
}
