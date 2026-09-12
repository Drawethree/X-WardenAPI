package dev.drawethree.xwarden.api.staff.punish;

import java.util.Optional;

/**
 * What came of asking for a punishment.
 *
 * @param outcome    what happened
 * @param punishment the record, when it was applied; {@code null} otherwise
 * @since 1.1.0
 */
public record PunishmentResult(PunishmentOutcome outcome, Punishment punishment) {

    /**
     * A punishment that was applied.
     *
     * @param punishment the record
     * @return the result
     * @since 1.1.0
     */
    public static PunishmentResult applied(Punishment punishment) {
        return new PunishmentResult(PunishmentOutcome.APPLIED, punishment);
    }

    /**
     * A punishment that was not.
     *
     * @param outcome why
     * @return the result
     * @since 1.1.0
     */
    public static PunishmentResult of(PunishmentOutcome outcome) {
        return new PunishmentResult(outcome, null);
    }

    /**
     * Whether it was applied.
     *
     * @return whether the outcome is {@link PunishmentOutcome#APPLIED}
     * @since 1.1.0
     */
    public boolean isApplied() {
        return this.outcome == PunishmentOutcome.APPLIED;
    }

    /**
     * The record, when it was applied.
     *
     * @return the punishment, or empty
     * @since 1.1.0
     */
    public Optional<Punishment> applied() {
        return Optional.ofNullable(this.punishment);
    }
}
