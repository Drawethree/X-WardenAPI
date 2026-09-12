package dev.drawethree.xwarden.api.network;

import java.util.UUID;

/**
 * Two accounts that look like the same hands, and how sure X-Warden is.
 *
 * <p>Read-only by design: every signal has an innocent explanation, so there is no action path
 * anywhere in X-Warden for a link.
 *
 * @param first      one account
 * @param firstName  its name
 * @param second     the other
 * @param secondName its name
 * @param confidence 0 - 100, from the weights in {@code warden.yml}
 * @param signals    what matched, in words: a shared connection, the same locale, sessions that
 *                   never overlap, and so on
 * @param computedAt when the correlation was last run, as epoch milliseconds
 * @since 1.0.0
 */
public record AccountLink(UUID first,
                          String firstName,
                          UUID second,
                          String secondName,
                          int confidence,
                          String signals,
                          long computedAt) {
}
