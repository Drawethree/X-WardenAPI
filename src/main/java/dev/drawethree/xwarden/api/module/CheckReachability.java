package dev.drawethree.xwarden.api.module;

/**
 * Whether a check can ever actually reach its action on this server's settings.
 *
 * <p>A finding adds {@code confidence / 100} to a violation level, and {@code vl-decay} is charged
 * <em>per minute</em>. A check that cannot report again before decay takes back more than one
 * finding is worth therefore has a ceiling, and if that ceiling sits under {@code vl-threshold} the
 * check goes on detecting exactly what it was built for and telling nobody, for ever, in silence.
 *
 * <p>Seven checks once shipped that way. The shipped defaults are guarded by a test, but an owner
 * who raises a threshold themselves - not knowing decay is per minute - reproduces it on their own
 * server, and nothing tells them.
 *
 * @param ceiling         the highest violation level this check can hold, given its cadence
 * @param threshold       the level it has to reach to act
 * @param decayPerCadence how much decays away between one finding and the next
 * @param cadenceSeconds  how often the check can report, from the settings in force
 * @param explanation     the same thing in a sentence, ready to show to a server owner
 */
public record CheckReachability(String moduleId,
                                String checkId,
                                boolean reachable,
                                double ceiling,
                                double threshold,
                                double decayPerCadence,
                                double cadenceSeconds,
                                String explanation) {
}
