package dev.drawethree.xwarden.api.automation;

import java.util.Map;

/**
 * How a calibration run is going.
 *
 * <p>Calibration watches ordinary players for a while and works out what normal looks like on this
 * server, so the automation baselines are measured rather than guessed. This is a reading of that
 * run taken at one moment; the run itself is live and mutable and does not leave the plugin.
 *
 * @param startedAt    when it began, as epoch milliseconds
 * @param endsAt       when it is due to finish
 * @param players      how many different people have been measured so far
 * @param finished     whether it has run its course
 * @param samplesByKey how many readings each baseline has collected
 * @since 1.0.0
 */
public record CalibrationStatus(long startedAt,
                                long endsAt,
                                int players,
                                boolean finished,
                                Map<String, Integer> samplesByKey) {

    /**
     * How long until the run is due to finish.
     *
     * @return milliseconds remaining, or {@code 0} once it is due
     * @since 1.0.0
     */
    public long remainingMillis() {
        return Math.max(0L, this.endsAt - System.currentTimeMillis());
    }
}
