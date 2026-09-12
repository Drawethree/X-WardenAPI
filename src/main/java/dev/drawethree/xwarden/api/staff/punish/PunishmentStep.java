package dev.drawethree.xwarden.api.staff.punish;

/**
 * One rung of a preset's ladder.
 *
 * @param type            what it does to the player
 * @param durationSeconds how long it lasts; {@link #PERMANENT} for no end, {@code 0} for a type
 *                        that has no duration (a warning, a kick)
 * @since 1.1.0
 */
public record PunishmentStep(PunishmentType type, long durationSeconds) {

    /**
     * The duration of a punishment with no end.
     *
     * @since 1.1.0
     */
    public static final long PERMANENT = -1L;

    /**
     * Whether this rung has no end.
     *
     * @return whether the duration is {@link #PERMANENT}
     * @since 1.1.0
     */
    public boolean isPermanent() {
        return this.durationSeconds == PERMANENT;
    }
}
