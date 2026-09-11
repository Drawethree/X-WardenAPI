package dev.drawethree.xwarden.api.staff.punish;

import java.util.Optional;

/** What came of asking for a punishment. */
public record PunishmentResult(PunishmentOutcome outcome, Punishment punishment) {

    public static PunishmentResult applied(Punishment punishment) {
        return new PunishmentResult(PunishmentOutcome.APPLIED, punishment);
    }

    public static PunishmentResult of(PunishmentOutcome outcome) {
        return new PunishmentResult(outcome, null);
    }

    public boolean isApplied() {
        return this.outcome == PunishmentOutcome.APPLIED;
    }

    public Optional<Punishment> applied() {
        return Optional.ofNullable(this.punishment);
    }
}
