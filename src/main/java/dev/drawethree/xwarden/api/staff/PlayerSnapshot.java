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
 * @param uuid             who
 * @param name             their name, as X-Warden knows it
 * @param online           whether they are on the server now. Most of the live fields below are
 *                         only populated while they are
 * @param joinedAt         when the current session began, as epoch milliseconds, or {@code 0}
 * @param lastSeen         when they were last on the server, or {@code 0} for never
 * @param watched          whether every payout they receive is being audited in full
 * @param frozen           whether they are being held still
 * @param freezeRemaining  milliseconds left on a freeze, or {@link Long#MAX_VALUE} for one with no
 *                         end on it
 * @param locale           the client's language setting, or {@code null}
 * @param clientBrand      what their client called itself, or {@code null}
 * @param automationScore  0 - 100, or {@code null} when not enough mining has been recorded to
 *                         score them. Absent is not the same as zero and should not be shown as it
 * @param levelsByModule   current violation level per module, after decay
 * @param openFlags        findings about them that nobody has marked as handled
 * @since 1.0.0
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

    /**
     * Whether enough mining has been recorded to score them.
     *
     * @return whether {@link #automationScore()} is set
     * @since 1.0.0
     */
    public boolean hasAutomationScore() {
        return this.automationScore != null;
    }
}
