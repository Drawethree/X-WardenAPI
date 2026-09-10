package dev.drawethree.xwarden.api.integrity;

import java.util.UUID;

public record FingerprintRecord(String uid,
                                String itemClass,
                                long firstSeen,
                                long lastSeen,
                                UUID lastHolder,
                                String lastLocation,
                                String status) {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_DUPLICATE = "DUPLICATE";
}
