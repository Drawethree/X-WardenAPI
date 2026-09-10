package dev.drawethree.xwarden.api.network;

import java.util.UUID;

public record SessionRecord(UUID player,
                            String playerName,
                            long joinAt,
                            long quitAt,
                            String addressHash,
                            String locale,
                            String clientBrand) {
}
