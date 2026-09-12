package dev.drawethree.xwarden.api.client;

import java.util.List;
import java.util.UUID;

/**
 * What X-Warden saw of the client a player joined with.
 *
 * @param player      the player
 * @param brand       what the client called itself, or {@code null} when the server could not say
 * @param channels    the plugin channels the client registered
 * @param verdict     what X-Warden concluded from all of it
 * @param matched     the signature names that matched, as spelled in {@code client-signatures.yml}
 * @param probed      whether the keybind probe ran and got an answer
 * @param probeHits   every probe line the client resolved, as {@code id -> what it became}
 * @param concludedAt when the join sequence finished, as epoch milliseconds
 * @since 1.1.0
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
