package dev.drawethree.xwarden.api.staff;

import java.util.Map;
import java.util.UUID;

/**
 * What X-Warden currently knows about one player, taken as a reading at one moment.
 *
 * <p>The live profile behind this is mutable, has setters on it, and exists only while the player
 * is online. Handing that out across a thread boundary would hand out the ability to change it, so
 * this is a copy instead.
 *
 * @param online           whether they are on the server now. Most of the live fields below are
 *                         only populated while they are
 * @param freezeRemaining  milliseconds left on a freeze, or {@link Long#MAX_VALUE} for one with no
 *                         end on it
 * @param automationScore  0 - 100, or {@code null} when not enough mining has been recorded to
 *                         score them. Absent is not the same as zero and should not be shown as it
 * @param levelsByModule   current violation level per module, after decay
 * @param openFlags        findings about them that nobody has marked as handled
 */
public record PlayerSnapshot(UUID uuid,
                             String name,
                             boolean online,
                             long joinedAt,
                             long lastSeen,
                             boolean watched,
                             boolean frozen,
                             long freezeRemaining,
                             String locale,
                             String clientBrand,
                             Integer automationScore,
                             Map<String, Integer> levelsByModule,
                             int openFlags) {

    public boolean hasAutomationScore() {
        return this.automationScore != null;
    }
}
