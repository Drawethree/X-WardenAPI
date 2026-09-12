package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One movement of money, as X-Warden recorded it.
 *
 * @param id              the row id
 * @param timestamp       when, as epoch milliseconds
 * @param player          whose balance moved
 * @param playerName      their name at the time
 * @param currency        which currency
 * @param direction       credit or debit
 * @param cause           what the economy said caused it, such as {@code MINING} or {@code PAY}
 * @param source          the plugin or subsystem that moved it
 * @param amount          how much, always positive; see {@link #signedAmount()}
 * @param balanceBefore   the balance before, or {@code null} when the economy could not say
 * @param balanceAfter    the balance after, or {@code null} likewise
 * @param multiplierChain the multipliers that were applied, in words, or {@code null}
 * @param correlationId   shared by both halves of a transfer, or {@code null}
 * @param counterparty    the other side of a transfer, or {@code null}
 * @since 1.0.0
 */
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

    /**
     * The amount with its direction applied.
     *
     * @return positive for a credit, negative for a debit
     * @since 1.0.0
     */
    public BigDecimal signedAmount() {
        return this.direction == Direction.DEBIT ? this.amount.negate() : this.amount;
    }
}
