package dev.drawethree.xwarden.api.economy;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Where the money went.
 *
 * <p>Mining income is tiered, and knowing which tier answers a question matters. Minute-by-minute
 * resolution lives in memory and only reaches back so far; disk holds one row per player per
 * currency per <em>hour</em>, written when that hour closes, plus a running lifetime total. Storing
 * every payout window instead would be around half a million rows a day at two hundred miners.
 *
 * <p>The practical consequence: the hour currently in progress is not on disk yet. A report that
 * reads {@link #income} alone shows nothing for the last sixty minutes. Ask {@link #liveIncome} for
 * that part, and {@link #liveIncomeCoversFrom} for where memory stops being able to answer.
 *
 * @since 1.0.0
 */
public interface XWardenLedgerAPI {

    /**
     * Discrete transactions for one player between two moments, newest first.
     *
     * @param player the player
     * @param from   the earliest moment, as epoch milliseconds
     * @param to     the latest
     * @param limit  the most to return
     * @return the entries, newest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<LedgerEntry> forPlayer(UUID player, long from, long to, int limit);

    /**
     * The most recent transactions on the server, newest first.
     *
     * @param limit the most to return
     * @return the entries, newest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<LedgerEntry> recent(int limit);

    /**
     * Mining income from disk: one bucket per hour that has closed.
     *
     * @param player the player
     * @param from   the earliest moment, as epoch milliseconds
     * @param to     the latest
     * @return the hourly buckets, oldest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<IncomeBucket> income(UUID player, long from, long to);

    /**
     * Mining income from memory: one bucket per minute, exact, but only back to
     * {@link #liveIncomeCoversFrom}.
     *
     * @param player the player
     * @param from   the earliest moment, as epoch milliseconds
     * @param to     the latest
     * @return the minute buckets, oldest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<IncomeBucket> liveIncome(UUID player, long from, long to);

    /**
     * The earliest moment the in-memory window can still answer for.
     *
     * @return that moment, as epoch milliseconds
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    long liveIncomeCoversFrom();

    /**
     * Everything this player has ever been credited in one currency, before a given moment.
     *
     * @param player   the player
     * @param currency which currency
     * @param before   the moment, as epoch milliseconds
     * @return the total, or zero
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    BigDecimal lifetimeCredited(UUID player, String currency, long before);

    /**
     * Everything this player has ever earned by mining in one currency.
     *
     * @param player   the player
     * @param currency which currency
     * @return the running total, or zero
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    BigDecimal lifetimeMining(UUID player, String currency);

    /**
     * Credits since a moment, by currency and then by player.
     *
     * @param from the moment, as epoch milliseconds
     * @return currency to player to total credited
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Map<String, Map<UUID, BigDecimal>> creditTotalsSince(long from);

    /**
     * Who paid whom since a moment, which is what cycle detection reads.
     *
     * @param from the moment, as epoch milliseconds
     * @return one edge per payment
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<PaymentEdge> paymentEdgesSince(long from);

    /**
     * Rows waiting to be written.
     *
     * <p>A number that only ever grows is a storage problem announcing itself well before anything
     * else notices.
     *
     * @return how many ledger rows have not reached the database yet
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    int pendingRows();

    /**
     * How much minute-resolution income is being held in memory.
     *
     * @return the number of entries in the live window
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    int liveIncomeEntries();

    /**
     * Writes a transaction into the ledger, so it appears in reports and can be rolled back. Use
     * this for money your own plugin moves outside the economy X-Warden is watching.
     *
     * @param player        whose balance moved
     * @param currency      which currency
     * @param amount        positive to credit, negative to debit
     * @param source        your plugin's name
     * @param correlationId shared between both halves of a transfer, or {@code null}
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void record(UUID player, String currency, BigDecimal amount, String source, String correlationId);
}
