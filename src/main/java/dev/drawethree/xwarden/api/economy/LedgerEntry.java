package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntry(long id,
                          long timestamp,
                          UUID player,
                          String playerName,
                          String currency,
                          Direction direction,
                          String cause,
                          String source,
                          BigDecimal amount,
                          BigDecimal balanceBefore,
                          BigDecimal balanceAfter,
                          String multiplierChain,
                          String correlationId,
                          UUID counterparty) {

    public BigDecimal signedAmount() {
        return this.direction == Direction.DEBIT ? this.amount.negate() : this.amount;
    }
}
