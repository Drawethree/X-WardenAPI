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
 */
public interface XWardenLedgerAPI {

    /** Discrete transactions for one player between two moments, newest first. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<LedgerEntry> forPlayer(UUID player, long from, long to, int limit);

    /** The most recent transactions on the server, newest first. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<LedgerEntry> recent(int limit);

    /** Mining income from disk: one bucket per hour that has closed. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<IncomeBucket> income(UUID player, long from, long to);

    /**
     * Mining income from memory: one bucket per minute, exact, but only back to
     * {@link #liveIncomeCoversFrom}.
     */
    @ThreadSafety(Requirement.ANY)
    List<IncomeBucket> liveIncome(UUID player, long from, long to);

    /** The earliest moment the in-memory window can still answer for. */
    @ThreadSafety(Requirement.ANY)
    long liveIncomeCoversFrom();

    /** Everything this player has ever been credited in one currency, before a given moment. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    BigDecimal lifetimeCredited(UUID player, String currency, long before);

    /** Everything this player has ever earned by mining in one currency. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    BigDecimal lifetimeMining(UUID player, String currency);

    /** Credits since a moment, by currency and then by player. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Map<String, Map<UUID, BigDecimal>> creditTotalsSince(long from);

    /** Who paid whom since a moment, which is what cycle detection reads. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<PaymentEdge> paymentEdgesSince(long from);

    /**
     * Rows waiting to be written.
     *
     * <p>A number that only ever grows is a storage problem announcing itself well before anything
     * else notices.
     */
    @ThreadSafety(Requirement.ANY)
    int pendingRows();

    /** How much minute-resolution income is being held in memory. */
    @ThreadSafety(Requirement.ANY)
    int liveIncomeEntries();

    /**
     * Writes a transaction into the ledger, so it appears in reports and can be rolled back. Use
     * this for money your own plugin moves outside the economy X-Warden is watching.
     *
     * @param amount        positive to credit, negative to debit
     * @param correlationId shared between both halves of a transfer, or null
     */
    @ThreadSafety(Requirement.ANY)
    void record(UUID player, String currency, BigDecimal amount, String source, String correlationId);
}
