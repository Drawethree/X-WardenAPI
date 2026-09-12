package dev.drawethree.xwarden.api.flags;

/**
 * What Warden found over a stretch of time, for the periodic digest.
 *
 * @param total       findings recorded in the window
 * @param players     how many different people they were about
 * @param busiestCheck the check that raised the most, or {@code null} when there were none
 * @param busiestCount how many that check raised
 * @since 1.0.0
 */
public record ViolationSummary(int total, int players, String busiestCheck, int busiestCount) {

    /**
     * A window in which nothing was found.
     *
     * @since 1.0.0
     */
    public static final ViolationSummary EMPTY = new ViolationSummary(0, 0, null, 0);

    /**
     * Whether nothing was found.
     *
     * @return whether the total is zero
     * @since 1.0.0
     */
    public boolean isEmpty() {
        return this.total <= 0;
    }
}
