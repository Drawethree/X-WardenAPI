package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One player paying another, which is what cycle detection is built from.
 *
 * @param from      who paid
 * @param to        who was paid
 * @param amount    how much
 * @param timestamp when, as epoch milliseconds
 * @since 1.0.0
 */
public record PaymentEdge(UUID from, UUID to, BigDecimal amount, long timestamp) {
}
