package dev.drawethree.xwarden.api.flags;

import dev.drawethree.xwarden.api.flags.WardenAction;
import dev.drawethree.xwarden.api.flags.EvidenceSnapshot;

import java.util.UUID;

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

    public Violation(long id, long timestamp, UUID player, String playerName, String moduleId,
                     String checkId, String scope, int confidence, int violationLevel,
                     WardenAction action, EvidenceSnapshot evidence) {
        this(id, timestamp, player, playerName, moduleId, checkId, scope, confidence, violationLevel,
                action, evidence, null);
    }

    public boolean isResolved() {
        return this.resolution != null;
    }

    // The check id, narrowed to what it was about - a currency, for the economy checks. Violation
    // levels are counted per scope, so a player laundering tokens does not accumulate a level
    // against the money they earn honestly.
    public String scopedCheckId() {
        return this.scope == null || this.scope.isEmpty() ? this.checkId : this.checkId + "@" + this.scope;
    }

    public Severity severity() {
        return Severity.of(this.confidence);
    }

    public boolean isHighSeverity() {
        return severity() == Severity.CRITICAL || this.action.isPunitive();
    }

    public boolean isBugNotice() {
        return "core".equals(this.moduleId);
    }
}
