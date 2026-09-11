package dev.drawethree.xwarden.api.network;

import java.util.List;
import java.util.UUID;

/**
 * One visit.
 *
 * @param clientBrand what the client called itself, or {@code null} when the server could not say
 * @param channels    the plugin channels the client registered, in the order they arrived
 */
public record SessionRecord(UUID player,
                            String playerName,
                            long joinAt,
                            long quitAt,
                            String addressHash,
                            String locale,
                            String clientBrand,
                            List<String> channels) {

    /** The shape before channels were recorded; kept so anything built against it still links. */
    public SessionRecord(UUID player, String playerName, long joinAt, long quitAt,
                         String addressHash, String locale, String clientBrand) {
        this(player, playerName, joinAt, quitAt, addressHash, locale, clientBrand, List.of());
    }
}
