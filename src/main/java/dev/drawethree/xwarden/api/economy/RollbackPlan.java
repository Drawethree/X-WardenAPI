package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * What reversing a stretch of one player's income would do, worked out before any of it is done.
 *
 * @param player        whose balance
 * @param playerName    their name, as staff should read it
 * @param from          the start of the stretch, as epoch milliseconds
 * @param to            its end
 * @param netByCurrency how much would be taken back in each currency
 * @param discreteRows  how many individual ledger entries contributed
 * @param incomeBuckets how many mining-income buckets contributed
 * @param notes         what staff should know before applying it: which part came from exact
 *                      minute-resolution memory and which from hourly summaries, and anything
 *                      that could not be reversed
 * @since 1.0.0
 */
public record RollbackPlan(UUID player,
                           String playerName,
                           long from,
                           long to,
                           Map<String, BigDecimal> netByCurrency,
                           int discreteRows,
                           int incomeBuckets,
                           List<String> notes) {

    /**
     * Whether there is anything to reverse.
     *
     * @return {@code true} when no currency would move
     * @since 1.0.0
     */
    public boolean isEmpty() {
        return this.netByCurrency.isEmpty();
    }
}
