package dev.drawethree.xwarden.api.staff.punish;

import java.util.UUID;

/**
 * One punishment as recorded, whether Warden applied it itself or handed it to a ban plugin.
 *
 * @param id              the row id
 * @param player          who
 * @param playerName      their name
 * @param type            what it does to them
 * @param preset          the preset it came from
 * @param step            which rung of the preset's ladder this was, counting from 0
 * @param reason          what the player was told
 * @param durationSeconds {@link PunishmentStep#PERMANENT} for no end, {@code 0} for an untimed type
 * @param issuedAt        when, as epoch milliseconds
 * @param expiresAt       when it ends, or {@code 0} when it never does or has no duration
 * @param staffId         who issued it, or {@code null} for the console, a plugin or a check
 * @param staffName       who issued it, in words
 * @param findingId       the X-Warden finding it was issued from, or {@code 0} when staff issued
 *                        it directly
 * @param liftedAt        {@code 0} until somebody lifts it
 * @param liftedBy        who lifted it, or {@code null}
 * @param liftReason      why, or {@code null}
 * @param applied         {@link #APPLIED_DELEGATED} when a configured command did the work,
 *                        {@link #APPLIED_BUILTIN} when Warden enforced it itself
 * @since 1.1.0
 */
public record Punishment(long id,
                         UUID player,
                         String playerName,
                         PunishmentType type,
                         String preset,
                         int step,
                         String reason,
                         long durationSeconds,
                         long issuedAt,
                         long expiresAt,
                         UUID staffId,
                         String staffName,
                         long liftedAt,
                         String liftedBy,
                         String liftReason,
                         long findingId,
                         String applied) {

    /**
     * A configured command template did the work.
     *
     * @since 1.1.0
     */
    public static final String APPLIED_DELEGATED = "delegated";

    /**
     * Warden enforced it itself.
     *
     * @since 1.1.0
     */
    public static final String APPLIED_BUILTIN = "builtin";

    /**
     * Whether it has no end.
     *
     * @return whether the duration is {@link PunishmentStep#PERMANENT}
     * @since 1.1.0
     */
    public boolean isPermanent() {
        return this.durationSeconds == PunishmentStep.PERMANENT;
    }

    /**
     * Whether somebody lifted it early.
     *
     * @return whether {@link #liftedAt()} is set
     * @since 1.1.0
     */
    public boolean isLifted() {
        return this.liftedAt > 0L;
    }

    /**
     * Whether it still binds at this moment: a timed or permanent type, not lifted, not expired.
     *
     * @param now the moment, as epoch milliseconds
     * @return whether it is active
     * @since 1.1.0
     */
    public boolean isActive(long now) {
        if (!this.type.isTimed() || isLifted()) {
            return false;
        }
        return isPermanent() || this.expiresAt > now;
    }
}
