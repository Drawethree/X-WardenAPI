package dev.drawethree.xwarden.api;

import java.util.Map;

/**
 * A player's automation score together with the individual signals that produced it.
 *
 * <p>This is the simplified shape the original API exposes. The richer
 * {@link dev.drawethree.xwarden.api.automation.AutomationScore} carries each signal's label,
 * reading and what an ordinary player looks like on the same measurement.
 *
 * @param composite     the overall score, 0 (unmistakably human) to 100 (unmistakably machine)
 * @param signals       each signal identifier mapped to its own 0.0 - 1.0 contribution
 * @param readings      each signal identifier mapped to a plain-language reading for staff
 * @param samples       how many block breaks the score was measured over
 * @param sessionMillis how long the measured mining session has run
 * @since 1.0.0
 */
public record AutomationBreakdown(int composite,
                                  Map<String, Double> signals,
                                  Map<String, String> readings,
                                  int samples,
                                  long sessionMillis) {
}
