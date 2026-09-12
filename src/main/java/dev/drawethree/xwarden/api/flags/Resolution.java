package dev.drawethree.xwarden.api.flags;

import org.jetbrains.annotations.Nullable;

/**
 * Who marked a finding handled, and when.
 *
 * @param at when, as epoch milliseconds
 * @param by the staff member as {@code Name (uuid)}, the console, or {@code Warden (reason)} when
 *           X-Warden withdrew its own finding; never {@code null}
 * @since 1.0.0
 */
public record Resolution(long at, String by) {

    /**
     * Reads a resolution off a stored row, where an unresolved finding has no timestamp.
     *
     * @param at the stored timestamp, or {@code 0} for none
     * @param by the stored actor, or {@code null}
     * @return the resolution, or {@code null} when {@code at} is not positive - which is how an
     *         open finding is represented on {@link Violation#resolution()}
     * @since 1.0.0
     */
    @Nullable
    public static Resolution of(long at, String by) {
        return at <= 0L ? null : new Resolution(at, by == null ? "unknown" : by);
    }
}
