package dev.drawethree.xwarden.api.staff.punish;

/**
 * One rung of a preset's ladder.
 *
 * @param durationSeconds how long it lasts; {@link #PERMANENT} for no end, {@code 0} for a type
 *                        that has no duration (a warning, a kick)
 */
public record PunishmentStep(PunishmentType type, long durationSeconds) {

    public static final long PERMANENT = -1L;

    public boolean isPermanent() {
        return this.durationSeconds == PERMANENT;
    }
}
