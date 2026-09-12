package dev.drawethree.xwarden.api;

import dev.drawethree.xwarden.api.automation.AutomationScore;
import dev.drawethree.xwarden.api.flags.EvidenceSnapshot;
import dev.drawethree.xwarden.api.flags.Resolution;
import dev.drawethree.xwarden.api.flags.Severity;
import dev.drawethree.xwarden.api.flags.WardenAction;
import dev.drawethree.xwarden.api.staff.punish.PunishmentType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The parts of the contract that carry logic rather than only shape, pinned so a change to any of
 * them is a deliberate one.
 */
class ApiContractTest {

    @Test
    void punishmentTypeParsesEitherSpellingAndAnswersEmptyForNonsense() {
        assertEquals(Optional.of(PunishmentType.IP_BAN), PunishmentType.parse("ip-ban"));
        assertEquals(Optional.of(PunishmentType.IP_BAN), PunishmentType.parse(" IP_BAN "));
        assertEquals(Optional.empty(), PunishmentType.parse("nope"));
        assertEquals(Optional.empty(), PunishmentType.parse(null));
        assertEquals("ip-mute", PunishmentType.IP_MUTE.key());
    }

    @Test
    void resolutionOfZeroMeansOpen() {
        assertNull(Resolution.of(0L, "somebody"));
        assertNull(Resolution.of(-5L, "somebody"));
        assertEquals("unknown", Resolution.of(5L, null).by());
    }

    @Test
    void builderSignalsCannotBeChangedFromOutside() {
        AutomationScore.Builder builder = new AutomationScore.Builder()
                .signal("interval-variance", "Timing variation", 0.9D, "0.4%", "above 15%");
        assertThrows(UnsupportedOperationException.class, () -> builder.signals().clear());
        assertEquals(1, builder.signals().size());
    }

    // These words are stored in evidence, so the thresholds behind them are part of the contract.
    @Test
    void signalVerdictTokensAreStable() {
        assertEquals("machine-like", signalAt(0.85D).verdict());
        assertEquals("unusual", signalAt(0.6D).verdict());
        assertEquals("borderline", signalAt(0.35D).verdict());
        assertEquals("normal", signalAt(0.34D).verdict());
        assertEquals(AutomationScore.Signal.NOT_MEASURED,
                new AutomationScore.Signal("x", "x", 1.0D, "unreadable", null, false).verdict());
        assertTrue(signalAt(0.6D).isSuspicious());
        assertFalse(signalAt(0.59D).isSuspicious());
    }

    @Test
    void severityBandsFollowTheirFloors() {
        assertEquals(Severity.CRITICAL, Severity.of(85));
        assertEquals(Severity.STRONG, Severity.of(84));
        assertEquals(Severity.WORTH_A_LOOK, Severity.of(50));
        assertEquals(Severity.WEAK, Severity.of(49));
        assertEquals(Severity.WEAK, Severity.of(-1));
    }

    @Test
    void wardenActionParsesLenientlyAndRanksPunishAboveCommand() {
        assertEquals(WardenAction.PUNISH, WardenAction.parse(" punish ", WardenAction.LOG));
        assertEquals(WardenAction.LOG, WardenAction.parse("", WardenAction.LOG));
        assertEquals(WardenAction.ALERT, WardenAction.parse("nonsense", WardenAction.ALERT));
        assertTrue(WardenAction.PUNISH.isPunitive());
        assertFalse(WardenAction.ALERT.isPunitive());
        assertTrue(WardenAction.PUNISH.getSeverity() > WardenAction.COMMAND.getSeverity());
    }

    @Test
    void evidenceSurvivesJsonAndNeverThrowsOnBadInput() {
        EvidenceSnapshot original = EvidenceSnapshot.builder("Summary")
                .add("Blocks", 1847L)
                .reading("Timing variation", "0.4%", "machine-like", "above 15%")
                .capturedAt(1234L)
                .build();
        EvidenceSnapshot copy = EvidenceSnapshot.fromJson(original.toJson());
        assertEquals("Summary", copy.getSummary());
        assertEquals(1234L, copy.getCapturedAt());
        assertEquals(2, copy.getEntries().size());
        assertEquals("machine-like", copy.getEntries().get(1).verdict());
        assertTrue(copy.getEntries().get(1).isInterpreted());
        assertFalse(copy.getEntries().get(0).isInterpreted());
        assertEquals("No evidence recorded", EvidenceSnapshot.fromJson("").getSummary());
        assertEquals("Evidence could not be read", EvidenceSnapshot.fromJson("{not json").getSummary());
    }

    private static AutomationScore.Signal signalAt(double score) {
        return new AutomationScore.Signal("x", "x", score, "reading", null);
    }
}
