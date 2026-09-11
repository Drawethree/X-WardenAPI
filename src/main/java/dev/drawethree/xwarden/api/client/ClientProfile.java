package dev.drawethree.xwarden.api.client;

import java.util.List;
import java.util.UUID;

/**
 * What X-Warden saw of the client a player joined with.
 *
 * @param brand    what the client called itself, or {@code null} when the server could not say
 * @param channels the plugin channels the client registered
 * @param matched  the signature names that matched, in {@code client-signatures.yml}
 * @param probed   whether the keybind probe ran and got an answer
 * @param probeHits every probe line the client resolved, as {@code id -> what it became}
 */
public record ClientProfile(UUID player,
                            String brand,
                            List<String> channels,
                            ClientVerdict verdict,
                            List<String> matched,
                            boolean probed,
                            List<String> probeHits,
                            long concludedAt) {
}
