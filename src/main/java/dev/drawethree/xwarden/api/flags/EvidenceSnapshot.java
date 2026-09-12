package dev.drawethree.xwarden.api.flags;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The measurements behind one finding, as staff see them.
 *
 * <p>A reading is only meaningful next to what normal looks like. "0.4% variation" means nothing on
 * its own; "0.4%, people are usually above 15%" is something a moderator can act on - so an entry
 * can carry a verdict and a typical value beside its own. Immutable once built; stored as JSON
 * with the finding and read back with {@link #fromJson}.
 *
 * @since 1.0.0
 */
public final class EvidenceSnapshot {

    private final String summary;
    private final List<Entry> entries;
    private final long capturedAt;

    private EvidenceSnapshot(String summary, List<Entry> entries, long capturedAt) {
        this.summary = summary;
        this.entries = Collections.unmodifiableList(entries);
        this.capturedAt = capturedAt;
    }

    /**
     * One sentence on what was found, in plain language.
     *
     * @return the summary, never {@code null}
     * @since 1.0.0
     */
    public String getSummary() {
        return this.summary;
    }

    /**
     * The readings, in the order they should be shown.
     *
     * @return an unmodifiable list
     * @since 1.0.0
     */
    public List<Entry> getEntries() {
        return this.entries;
    }

    /**
     * When the measurements were taken.
     *
     * @return epoch milliseconds
     * @since 1.0.0
     */
    public long getCapturedAt() {
        return this.capturedAt;
    }

    /**
     * Serialises the snapshot the way X-Warden stores it.
     *
     * @return a JSON object with {@code summary}, {@code capturedAt} and {@code entries}
     * @since 1.0.0
     */
    public String toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("summary", this.summary);
        root.addProperty("capturedAt", this.capturedAt);
        JsonArray array = new JsonArray();
        for (Entry entry : this.entries) {
            JsonObject element = new JsonObject();
            element.addProperty("label", entry.label());
            element.addProperty("value", entry.value());
            if (entry.verdict() != null) {
                element.addProperty("verdict", entry.verdict());
            }
            if (entry.typical() != null) {
                element.addProperty("typical", entry.typical());
            }
            array.add(element);
        }
        root.add("entries", array);
        return root.toString();
    }

    /**
     * Reads a snapshot back from {@link #toJson}.
     *
     * <p>Never throws: an empty string yields a snapshot that says no evidence was recorded, and
     * malformed JSON yields one that says it could not be read, so a corrupt row still draws.
     *
     * @param json the stored text, or {@code null}
     * @return the snapshot
     * @since 1.0.0
     */
    public static EvidenceSnapshot fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return builder("No evidence recorded").build();
        }
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            Builder builder = builder(root.has("summary") ? root.get("summary").getAsString() : "");
            if (root.has("capturedAt")) {
                builder.capturedAt(root.get("capturedAt").getAsLong());
            }
            if (root.has("entries")) {
                for (var element : root.getAsJsonArray("entries")) {
                    JsonObject entry = element.getAsJsonObject();
                    builder.entries.add(new Entry(
                            entry.get("label").getAsString(),
                            entry.get("value").getAsString(),
                            entry.has("verdict") ? entry.get("verdict").getAsString() : null,
                            entry.has("typical") ? entry.get("typical").getAsString() : null));
                }
            }
            return builder.build();
        } catch (RuntimeException malformed) {
            return builder("Evidence could not be read").build();
        }
    }

    /**
     * Starts a snapshot.
     *
     * @param summary one sentence on what was found
     * @return a builder
     * @since 1.0.0
     */
    public static Builder builder(String summary) {
        return new Builder(summary);
    }

    /**
     * One reading.
     *
     * @param label   what was measured, as staff see it
     * @param value   the reading, as text
     * @param verdict the word for it - {@code machine-like}, {@code unusual}, {@code borderline},
     *                {@code normal} - or {@code null} for a plain fact with no interpretation
     * @param typical what an ordinary player reads on the same measurement, or {@code null}
     * @since 1.0.0
     */
    public record Entry(String label, String value, String verdict, String typical) {

        /**
         * Whether this reading carries a verdict, as opposed to being a plain fact.
         *
         * @return whether {@link #verdict()} is set
         * @since 1.0.0
         */
        public boolean isInterpreted() {
            return this.verdict != null;
        }
    }

    /**
     * Assembles a snapshot one reading at a time.
     *
     * @since 1.0.0
     */
    public static final class Builder {

        private final String summary;
        private final List<Entry> entries = new ArrayList<>();
        private long capturedAt = System.currentTimeMillis();

        private Builder(String summary) {
            this.summary = summary == null ? "" : summary;
        }

        /**
         * Adds a plain fact.
         *
         * @param label what it is
         * @param value the text, or {@code null} for empty
         * @return this builder
         * @since 1.0.0
         */
        public Builder add(String label, String value) {
            this.entries.add(new Entry(label, value == null ? "" : value, null, null));
            return this;
        }

        /**
         * Adds a plain whole number.
         *
         * @param label what it is
         * @param value the number
         * @return this builder
         * @since 1.0.0
         */
        public Builder add(String label, long value) {
            return add(label, Long.toString(value));
        }

        /**
         * Adds an interpreted reading: the value, the word for it, and what normal looks like.
         *
         * @param label   what was measured
         * @param value   the reading, or {@code null} for empty
         * @param verdict the word for it
         * @param typical what an ordinary player reads, or {@code null}
         * @return this builder
         * @since 1.0.0
         */
        public Builder reading(String label, String value, String verdict, String typical) {
            this.entries.add(new Entry(label, value == null ? "" : value, verdict, typical));
            return this;
        }

        /**
         * Adds a fraction as a percentage with two decimals.
         *
         * @param label    what it is
         * @param fraction 0.0 - 1.0
         * @return this builder
         * @since 1.0.0
         */
        public Builder addPercent(String label, double fraction) {
            return add(label, String.format("%.2f%%", fraction * 100.0D));
        }

        /**
         * Adds a number with three decimals.
         *
         * @param label what it is
         * @param value the number
         * @return this builder
         * @since 1.0.0
         */
        public Builder addNumber(String label, double value) {
            return add(label, String.format("%.3f", value));
        }

        /**
         * Sets when the measurements were taken. Defaults to now.
         *
         * @param capturedAt epoch milliseconds
         * @return this builder
         * @since 1.0.0
         */
        public Builder capturedAt(long capturedAt) {
            this.capturedAt = capturedAt;
            return this;
        }

        /**
         * Finishes the snapshot.
         *
         * @return the snapshot
         * @since 1.0.0
         */
        public EvidenceSnapshot build() {
            return new EvidenceSnapshot(this.summary, new ArrayList<>(this.entries), this.capturedAt);
        }
    }
}
