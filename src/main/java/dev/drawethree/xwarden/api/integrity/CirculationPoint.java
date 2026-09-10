package dev.drawethree.xwarden.api.integrity;

public record CirculationPoint(String itemClass,
                               long timestamp,
                               long total,
                               long knownSourceDelta) {
}
