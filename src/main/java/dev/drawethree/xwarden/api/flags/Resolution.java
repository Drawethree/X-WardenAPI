package dev.drawethree.xwarden.api.flags;

public record Resolution(long at, String by) {

    public static Resolution of(long at, String by) {
        return at <= 0L ? null : new Resolution(at, by == null ? "unknown" : by);
    }
}
