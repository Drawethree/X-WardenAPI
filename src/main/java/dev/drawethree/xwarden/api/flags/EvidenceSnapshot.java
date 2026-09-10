package dev.drawethree.xwarden.api.flags;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A reading is only meaningful next to what normal looks like. "0.4% variation" means nothing on its
// own; "0.4%, people are usually above 15%" is something a moderator can act on.
public final class EvidenceSnapshot {

    private final String summary;
    private final List<Entry> entries;
    private final long capturedAt;

    private EvidenceSnapshot(String summary, List<Entry> entries, long capturedAt) {
        this.summary = summary;
        this.entries = Collections.unmodifiableList(entries);
        this.capturedAt = capturedAt;
    }

    public String getSummary() {
        return this.summary;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public long getCapturedAt() {
        return this.capturedAt;
    }

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

    public static Builder builder(String summary) {
        return new Builder(summary);
    }

    public record Entry(String label, String value, String verdict, String typical) {

        public boolean isInterpreted() {
            return this.verdict != null;
        }
    }

    public static final class Builder {

        private final String summary;
        private final List<Entry> entries = new ArrayList<>();
        private long capturedAt = System.currentTimeMillis();

        private Builder(String summary) {
            this.summary = summary == null ? "" : summary;
        }

        public Builder add(String label, String value) {
            this.entries.add(new Entry(label, value == null ? "" : value, null, null));
            return this;
        }

        public Builder add(String label, long value) {
            return add(label, Long.toString(value));
        }

        public Builder reading(String label, String value, String verdict, String typical) {
            this.entries.add(new Entry(label, value == null ? "" : value, verdict, typical));
            return this;
        }

        public Builder addPercent(String label, double fraction) {
            return add(label, String.format("%.2f%%", fraction * 100.0D));
        }

        public Builder addNumber(String label, double value) {
            return add(label, String.format("%.3f", value));
        }

        public Builder capturedAt(long capturedAt) {
            this.capturedAt = capturedAt;
            return this;
        }

        public EvidenceSnapshot build() {
            return new EvidenceSnapshot(this.summary, new ArrayList<>(this.entries), this.capturedAt);
        }
    }
}
