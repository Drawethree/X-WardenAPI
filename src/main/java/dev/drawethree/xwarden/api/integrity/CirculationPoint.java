package dev.drawethree.xwarden.api.integrity;

/**
 * How many of one class of item existed on the server at one moment, as counted by a sweep.
 *
 * <p>The circulation-anomaly check reads these: a class growing faster than its known sources can
 * account for is being duplicated somewhere the live index cannot see.
 *
 * @param itemClass        the class, such as {@code PICKAXE} or {@code CUSTOM}
 * @param timestamp        when the sweep finished, as epoch milliseconds
 * @param total            how many tracked items of that class it saw
 * @param knownSourceDelta how many were created by a known source since the previous point
 * @since 1.0.0
 */
public record CirculationPoint(String itemClass,
                               long timestamp,
                               long total,
                               long knownSourceDelta) {
}
