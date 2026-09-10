package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

public record Sighting(String uid,
                       long scanId,
                       UUID holder,
                       String holderName,
                       String container,
                       int slot) {
}
