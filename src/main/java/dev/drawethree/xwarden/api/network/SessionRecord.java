package dev.drawethree.xwarden.api.network;

import java.util.List;
import java.util.UUID;

/**
 * One visit.
 *
 * @param player      who
 * @param playerName  their name at the time
 * @param joinAt      when they joined, as epoch milliseconds
 * @param quitAt      when they left, or {@code 0} while they are still online
 * @param addressHash a salted hash of the address they came from; the address itself is never
 *                    stored
 * @param locale      the client's language setting
 * @param clientBrand what the client called itself, or {@code null} when the server could not say
 * @param channels    the plugin channels the client registered, in the order they arrived
 * @since 1.0.0
 */
public record SessionRecord(UUID player,
                            String playerName,
                            long joinAt,
                            long quitAt,
                            String addressHash,
                            String locale,
                            String clientBrand,
                            List<String> channels) {

    /**
     * The shape before channels were recorded; kept so anything built against it still links.
     *
     * @param player      who
     * @param playerName  their name
     * @param joinAt      when they joined
     * @param quitAt      when they left, or {@code 0}
     * @param addressHash the salted address hash
     * @param locale      the client's language
     * @param clientBrand the client's brand, or {@code null}
     * @since 1.0.0
     */
    public SessionRecord(UUID player, String playerName, long joinAt, long quitAt,
                         String addressHash, String locale, String clientBrand) {
        this(player, playerName, joinAt, quitAt, addressHash, locale, clientBrand, List.of());
    }
}
