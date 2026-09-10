package dev.drawethree.xwarden.api.network;

import java.util.UUID;

public record AccountLink(UUID first,
                          String firstName,
                          UUID second,
                          String secondName,
                          int confidence,
                          String signals,
                          long computedAt) {
}
