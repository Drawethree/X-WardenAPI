package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RollbackPlan(UUID player,
                           String playerName,
                           long from,
                           long to,
                           Map<String, BigDecimal> netByCurrency,
                           int discreteRows,
                           int incomeBuckets,
                           List<String> notes) {

    public boolean isEmpty() {
        return this.netByCurrency.isEmpty();
    }
}
