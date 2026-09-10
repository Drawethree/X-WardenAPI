package dev.drawethree.xwarden.api.flags;

import java.util.List;

/**
 * One page of findings, and how many there were altogether.
 *
 * <p>The total is what the filter matched, not what this page holds. Counting the rows that came
 * back only ever answers how big the page was, which is no use to anything that has to say
 * "showing 20 of 431".
 *
 * @param rows   the findings on this page, newest first
 * @param offset how many were skipped to reach it
 * @param limit  the most this page could have held
 * @param total  how many the same filter matched in all
 */
public record FlagPage(List<Violation> rows, int offset, int limit, int total) {

    public static final FlagPage EMPTY = new FlagPage(List.of(), 0, 0, 0);

    public boolean hasMore() {
        return this.offset + this.rows.size() < this.total;
    }
}
