package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * One player's mining income in one currency over one window.
 *
 * <p>In memory the window is a minute; on disk it is an hour, plus one running lifetime total per
 * player and currency. Two buckets for the same player, currency and window fold into one with
 * {@link #mergedWith}.
 *
 * @param player            whose income
 * @param playerName        their name, or {@code null} when X-Warden never saw them join
 * @param currency          which currency
 * @param windowStart       the start of the window, as epoch milliseconds
 * @param windowEnd         its end
 * @param grossBase         what the payouts were worth before anything multiplied them
 * @param netCredited       what actually reached the balance
 * @param blocks            how many blocks were broken
 * @param payouts           how many payouts there were
 * @param multiplierMin     the smallest total multiplier measured, or {@code null} when none was
 * @param multiplierMax     the largest, or {@code null}
 * @param multiplierAverage the average over the audited payouts, or {@code null}
 * @param auditSamples      how many payouts were measured in full for the multiplier figures
 * @since 1.0.0
 */
public record IncomeBucket(UUID player,
                           String playerName,
                           String currency,
                           long windowStart,
                           long windowEnd,
                           BigDecimal grossBase,
                           BigDecimal netCredited,
                           long blocks,
                           int payouts,
                           BigDecimal multiplierMin,
                           BigDecimal multiplierMax,
                           BigDecimal multiplierAverage,
                           int auditSamples) {

    private static final int MULTIPLIER_SCALE = 8;

    /**
     * Two readings of the same player, currency and window, added together.
     *
     * <p>The rollup folds minutes into an hour with this and the income table folds a new hour into
     * the row it already holds with it, so both have to agree: a server restarted mid-hour writes
     * that hour twice, and the second write replacing the first is how an hour of somebody's mining
     * used to disappear.
     *
     * @param other the other reading, or {@code null} for none
     * @return the sum, spanning both windows
     * @since 1.0.0
     */
    public IncomeBucket mergedWith(IncomeBucket other) {
        if (other == null) {
            return this;
        }
        int samples = this.auditSamples + other.auditSamples();
        return new IncomeBucket(this.player,
                other.playerName() == null ? this.playerName : other.playerName(),
                this.currency,
                this.windowStart,
                Math.max(this.windowEnd, other.windowEnd()),
                this.grossBase.add(other.grossBase()),
                this.netCredited.add(other.netCredited()),
                this.blocks + other.blocks(),
                this.payouts + other.payouts(),
                smallest(this.multiplierMin, other.multiplierMin()),
                largest(this.multiplierMax, other.multiplierMax()),
                weightedAverage(this, other, samples),
                samples);
    }

    private static BigDecimal weightedAverage(IncomeBucket left, IncomeBucket right, int samples) {
        if (samples == 0) {
            return null;
        }
        BigDecimal total = BigDecimal.ZERO;
        if (left.multiplierAverage() != null) {
            total = total.add(left.multiplierAverage().multiply(BigDecimal.valueOf(left.auditSamples())));
        }
        if (right.multiplierAverage() != null) {
            total = total.add(right.multiplierAverage().multiply(BigDecimal.valueOf(right.auditSamples())));
        }
        return total.divide(BigDecimal.valueOf(samples), MULTIPLIER_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal smallest(BigDecimal left, BigDecimal right) {
        if (left == null) {
            return right;
        }
        return right == null ? left : left.min(right);
    }

    private static BigDecimal largest(BigDecimal left, BigDecimal right) {
        if (left == null) {
            return right;
        }
        return right == null ? left : left.max(right);
    }
}
