package dev.drawethree.xwarden.api.flags;

/**
 * What Warden found over a stretch of time, for the periodic digest.
 *
 * @param total       findings recorded in the window
 * @param players     how many different people they were about
 * @param busiestCheck the check that raised the most, or {@code null} when there were none
 * @param busiestCount how many that check raised
 */
public record ViolationSummary(int total, int players, String busiestCheck, int busiestCount) {

    public static final ViolationSummary EMPTY = new ViolationSummary(0, 0, null, 0);

    public boolean isEmpty() {
        return this.total <= 0;
    }
}
