package dev.drawethree.xwarden.api.automation;

/**
 * One baseline calibration would change, and the reading behind it.
 *
 * @param key       which baseline, as it is spelled in the configuration
 * @param current   what it is set to now
 * @param suggested what the players actually measured
 * @param samples   how many readings that is based on. A suggestion drawn from a handful of samples
 *                  is a guess with a decimal point on it, which is why the count travels with it
 * @since 1.0.0
 */
public record BaselineSuggestion(String key, double current, double suggested, int samples) {

    /**
     * Whether this is worth acting on.
     *
     * <p>A baseline that moves by a couple of percent is measurement noise, not a finding about
     * the server. Ten percent is where a suggestion starts meaning something.
     *
     * @return {@code true} when the suggestion differs from the current value by ten percent or
     *         more, or when there is no current value at all
     * @since 1.0.0
     */
    public boolean isMeaningfullyDifferent() {
        if (this.current <= 0.0D) {
            return true;
        }
        return Math.abs(this.suggested - this.current) / this.current >= 0.10D;
    }
}
