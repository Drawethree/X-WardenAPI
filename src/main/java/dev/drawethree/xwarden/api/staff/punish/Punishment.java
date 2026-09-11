package dev.drawethree.xwarden.api.staff.punish;

import java.util.UUID;

/**
 * One punishment as recorded, whether Warden applied it itself or handed it to a ban plugin.
 *
 * @param step            which rung of the preset's ladder this was, counting from 0
 * @param durationSeconds {@link PunishmentStep#PERMANENT} for no end, {@code 0} for an untimed type
 * @param expiresAt       when it ends, or {@code 0} when it never does or has no duration
 * @param findingId       the X-Warden finding it was issued from, or {@code 0} when staff issued
 *                        it directly
 * @param liftedAt        {@code 0} until somebody lifts it
 * @param applied         {@link #APPLIED_DELEGATED} when a configured command did the work,
 *                        {@link #APPLIED_BUILTIN} when Warden enforced it itself
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

    public static final String APPLIED_DELEGATED = "delegated";
    public static final String APPLIED_BUILTIN = "builtin";

    public boolean isPermanent() {
        return this.durationSeconds == PunishmentStep.PERMANENT;
    }

    public boolean isLifted() {
        return this.liftedAt > 0L;
    }

    /** Whether it still binds at this moment: a timed or permanent type, not lifted, not expired. */
    public boolean isActive(long now) {
        if (!this.type.isTimed() || isLifted()) {
            return false;
        }
        return isPermanent() || this.expiresAt > now;
    }
}
