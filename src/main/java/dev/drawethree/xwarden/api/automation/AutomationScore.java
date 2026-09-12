package dev.drawethree.xwarden.api.automation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * How much like a machine somebody is mining, with every signal that went into the answer.
 *
 * <p>Speed is not the signal; consistency is. Each {@link Signal} is one measurement - timing
 * variation, timing spread, repeating loops, camera drift, session continuity, movement repetition
 * - scored from 0.0 (ordinary) to 1.0 (machine-like), and each carries what an ordinary player
 * looks like on the same measurement, because "0.4% variation" means nothing on its own.
 *
 * @param composite     the overall score, 0 (unmistakably human) to 100 (unmistakably machine)
 * @param signals       every signal, measured or not, in the order they are shown to staff
 * @param samples       how many block breaks the score was measured over
 * @param sessionMillis how long the measured mining session has run
 * @param measurements  what each signal actually measured, keyed by the baseline it was measured
 *                      against, which is what calibration reads
 * @since 1.0.0
 */
public record AutomationScore(int composite,
                              List<Signal> signals,
                              int samples,
                              long sessionMillis,
                              Map<String, Double> measurements) {

    /**
     * A score for somebody who has not mined enough to be scored.
     *
     * @param samples how many block breaks have been recorded so far
     * @return a score of zero with no signals, which {@link #isMeasured()} reports as unmeasured
     * @since 1.0.0
     */
    public static AutomationScore insufficient(int samples) {
        return new AutomationScore(0, List.of(), samples, 0L, Map.of());
    }

    /**
     * What a signal actually measured, keyed by the baseline it was measured against.
     *
     * @param baselineKey the baseline, as it is spelled in the configuration
     * @param fallback    what to answer when that baseline was not measured
     * @return the measurement, or the fallback
     * @since 1.0.0
     */
    public double measurement(String baselineKey, double fallback) {
        return this.measurements.getOrDefault(baselineKey, fallback);
    }

    /**
     * Whether at least one signal could be read.
     *
     * @return {@code false} for {@link #insufficient} and for a session where nothing could be
     *         measured; such a score is not a zero and should not be shown as one
     * @since 1.0.0
     */
    public boolean isMeasured() {
        return this.signals.stream().anyMatch(Signal::measured);
    }

    /**
     * One signal by its identifier.
     *
     * @param id the signal id, such as {@code interval-variance}
     * @return the signal, or empty when this score does not carry it
     * @since 1.0.0
     */
    public Optional<Signal> signal(String id) {
        return this.signals.stream().filter(signal -> signal.id().equals(id)).findFirst();
    }

    /**
     * The measured signals' scores by id, in order.
     *
     * @return signal id to its 0.0 - 1.0 score; unmeasured signals are left out
     * @since 1.0.0
     */
    public Map<String, Double> scoresById() {
        Map<String, Double> scores = new LinkedHashMap<>();
        this.signals.stream().filter(Signal::measured)
                .forEach(signal -> scores.put(signal.id(), signal.score()));
        return scores;
    }

    /**
     * The measured signals' readings by id, in order, each with what an ordinary player looks like.
     *
     * @return signal id to {@link Signal#readingWithTypical()}; unmeasured signals are left out
     * @since 1.0.0
     */
    public Map<String, String> readingsById() {
        Map<String, String> readings = new LinkedHashMap<>();
        this.signals.stream().filter(Signal::measured)
                .forEach(signal -> readings.put(signal.id(), signal.readingWithTypical()));
        return readings;
    }

    /**
     * One measurement, next to what normal looks like.
     *
     * @param id       the signal id, as it is spelled in the configuration and in evidence
     * @param label    what staff see it called
     * @param score    0.0 (ordinary) to 1.0 (machine-like); {@code 0.0} when not measured
     * @param observed the reading, in words, or the reason it could not be taken
     * @param typical  what an ordinary player reads on the same measurement, or {@code null} when
     *                 no baseline exists for it
     * @param measured whether the reading could be taken at all
     * @since 1.0.0
     */
    public record Signal(String id, String label, double score, String observed, String typical,
                         boolean measured) {

        /**
         * The verdict of a signal that could not be read.
         *
         * @since 1.0.0
         */
        public static final String NOT_MEASURED = "not measured";

        /**
         * A measured signal.
         *
         * @param id       the signal id
         * @param label    what staff see it called
         * @param score    0.0 - 1.0
         * @param observed the reading, in words
         * @param typical  what an ordinary player reads, or {@code null}
         * @since 1.0.0
         */
        public Signal(String id, String label, double score, String observed, String typical) {
            this(id, label, score, observed, typical, true);
        }

        /**
         * The word a moderator reads instead of a percentage.
         *
         * <p>These tokens are what X-Warden stores in evidence, so they are stable:
         * {@code machine-like} at 0.85 and above, {@code unusual} at 0.6, {@code borderline} at
         * 0.35, {@code normal} below that, and {@link #NOT_MEASURED} for a signal that could not
         * be read. How each is displayed is the server owner's to configure.
         *
         * @return the verdict token
         * @since 1.0.0
         */
        public String verdict() {
            if (!this.measured) {
                return NOT_MEASURED;
            }
            if (this.score >= 0.85D) {
                return "machine-like";
            }
            if (this.score >= 0.6D) {
                return "unusual";
            }
            if (this.score >= 0.35D) {
                return "borderline";
            }
            return "normal";
        }

        /**
         * Whether this signal on its own reads as unusual or worse.
         *
         * @return {@code true} for a measured score of 0.6 or above
         * @since 1.0.0
         */
        public boolean isSuspicious() {
            return this.measured && this.score >= 0.6D;
        }

        /**
         * The reading with what an ordinary player looks like appended.
         *
         * @return {@code observed}, followed by {@code (people: typical)} when a baseline exists
         * @since 1.0.0
         */
        public String readingWithTypical() {
            return this.typical == null || this.typical.isEmpty()
                    ? this.observed
                    : this.observed + " (people: " + this.typical + ")";
        }
    }

    /**
     * Assembles a score one signal at a time. X-Warden's scorer uses this; a plugin reporting
     * scores of its own may too.
     *
     * @since 1.0.0
     */
    public static final class Builder {

        private final List<Signal> signals = new ArrayList<>();
        private final Map<String, Double> measurements = new LinkedHashMap<>();

        /**
         * Creates an empty builder.
         *
         * @since 1.0.0
         */
        public Builder() {
        }

        /**
         * Records what a signal measured, so calibration can read the population.
         *
         * @param baselineKey the baseline, as it is spelled in the configuration
         * @param value       the raw measurement
         * @return this builder
         * @since 1.0.0
         */
        public Builder measure(String baselineKey, double value) {
            this.measurements.put(baselineKey, value);
            return this;
        }

        /**
         * Adds a measured signal. The value is clamped to 0.0 - 1.0.
         *
         * @param id       the signal id
         * @param label    what staff see it called
         * @param value    the score, clamped to 0.0 - 1.0
         * @param observed the reading, in words
         * @param typical  what an ordinary player reads, or {@code null}
         * @return this builder
         * @since 1.0.0
         */
        public Builder signal(String id, String label, double value, String observed, String typical) {
            this.signals.add(new Signal(id, label, Math.max(0.0D, Math.min(1.0D, value)),
                    observed, typical));
            return this;
        }

        /**
         * Adds a signal that could not be read, with the reason in place of the reading.
         *
         * <p>It is still shown, because an absent row is indistinguishable from a clean one to the
         * person reading the screen.
         *
         * @param id     the signal id
         * @param label  what staff see it called
         * @param reason why it could not be measured, in words
         * @return this builder
         * @since 1.0.0
         */
        public Builder unmeasured(String id, String label, String reason) {
            this.signals.add(new Signal(id, label, 0.0D, reason, null, false));
            return this;
        }

        /**
         * The signals added so far, for a scorer that combines them into the composite.
         *
         * @return a read-only view of the signals, in the order they were added
         * @since 1.0.0
         */
        public List<Signal> signals() {
            return Collections.unmodifiableList(this.signals);
        }

        /**
         * Finishes the score.
         *
         * @param composite     the overall score, 0 - 100
         * @param samples       how many block breaks it was measured over
         * @param sessionMillis how long the mining session has run
         * @return the score
         * @since 1.0.0
         */
        public AutomationScore build(int composite, int samples, long sessionMillis) {
            return new AutomationScore(composite,
                    Collections.unmodifiableList(new ArrayList<>(this.signals)), samples, sessionMillis,
                    Map.copyOf(this.measurements));
        }
    }
}
