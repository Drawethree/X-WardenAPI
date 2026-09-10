package dev.drawethree.xwarden.api.economy;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentEdge(UUID from, UUID to, BigDecimal amount, long timestamp) {
}
