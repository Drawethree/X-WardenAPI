package dev.drawethree.xwarden.api.automation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record AutomationScore(int composite,
                              List<Signal> signals,
                              int samples,
                              long sessionMillis,
                              Map<String, Double> measurements) {

    public static AutomationScore insufficient(int samples) {
        return new AutomationScore(0, List.of(), samples, 0L, Map.of());
    }

    // What each signal actually measured, keyed by the baseline it was measured against.
    public double measurement(String baselineKey, double fallback) {
        return this.measurements.getOrDefault(baselineKey, fallback);
    }

    public boolean isMeasured() {
        return this.signals.stream().anyMatch(Signal::measured);
    }

    public Optional<Signal> signal(String id) {
        return this.signals.stream().filter(signal -> signal.id().equals(id)).findFirst();
    }

    public Map<String, Double> scoresById() {
        Map<String, Double> scores = new LinkedHashMap<>();
        this.signals.stream().filter(Signal::measured)
                .forEach(signal -> scores.put(signal.id(), signal.score()));
        return scores;
    }

    public Map<String, String> readingsById() {
        Map<String, String> readings = new LinkedHashMap<>();
        this.signals.stream().filter(Signal::measured)
                .forEach(signal -> readings.put(signal.id(), signal.readingWithTypical()));
        return readings;
    }

    public record Signal(String id, String label, double score, String observed, String typical,
                         boolean measured) {

        public static final String NOT_MEASURED = "not measured";

        public Signal(String id, String label, double score, String observed, String typical) {
            this(id, label, score, observed, typical, true);
        }

        // The word a moderator reads instead of a percentage.
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

        public boolean isSuspicious() {
            return this.measured && this.score >= 0.6D;
        }

        public String readingWithTypical() {
            return this.typical == null || this.typical.isEmpty()
                    ? this.observed
                    : this.observed + " (people: " + this.typical + ")";
        }
    }

    public static final class Builder {

        private final List<Signal> signals = new ArrayList<>();
        private final Map<String, Double> measurements = new LinkedHashMap<>();

        /** Records what a signal measured, so /xwarden calibrate can read the population. */
        public Builder measure(String baselineKey, double value) {
            this.measurements.put(baselineKey, value);
            return this;
        }

        public Builder signal(String id, String label, double value, String observed, String typical) {
            this.signals.add(new Signal(id, label, Math.max(0.0D, Math.min(1.0D, value)),
                    observed, typical));
            return this;
        }

        // A signal that could not be read is still shown, with the reason in place of the reading:
        // an absent row is indistinguishable from a clean one to the person reading the screen.
        public Builder unmeasured(String id, String label, String reason) {
            this.signals.add(new Signal(id, label, 0.0D, reason, null, false));
            return this;
        }

        public List<Signal> signals() {
            return this.signals;
        }

        public AutomationScore build(int composite, int samples, long sessionMillis) {
            return new AutomationScore(composite,
                    Collections.unmodifiableList(new ArrayList<>(this.signals)), samples, sessionMillis,
                    Map.copyOf(this.measurements));
        }
    }
}
