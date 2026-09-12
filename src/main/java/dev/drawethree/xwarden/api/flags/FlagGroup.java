package dev.drawethree.xwarden.api.flags;

import java.util.UUID;

/**
 * Every open finding of one check on one player, counted as a single ongoing problem.
 *
 * @param player        who the findings are about
 * @param playerName    their name
 * @param moduleId      the module the check belongs to
 * @param checkId       the check
 * @param scope         what the findings were narrowed to, or {@code null}
 * @param count         how many open findings are in the group
 * @param topConfidence the highest confidence any finding in the group reached
 * @param firstSeen     when the oldest was recorded, as epoch milliseconds
 * @param lastSeen      when the newest was
 * @param latestId      the newest finding in the group, which is the one whose evidence is shown
 * @since 1.0.0
 */
public record FlagGroup(UUID player,
                        String playerName,
                        String moduleId,
                        String checkId,
                        String scope,
                        int count,
                        int topConfidence,
                        long firstSeen,
                        long lastSeen,
                        long latestId) {

    /**
     * A group holding one finding.
     *
     * @param flag the finding
     * @return a group of one
     * @since 1.0.0
     */
    public static FlagGroup ofSingle(Violation flag) {
        return new FlagGroup(flag.player(), flag.playerName(), flag.moduleId(), flag.checkId(),
                flag.scope(), 1, flag.confidence(), flag.timestamp(), flag.timestamp(), flag.id());
    }

    /**
     * Whether the same check has fired more than once on this player.
     *
     * @return whether {@link #count()} is above one
     * @since 1.0.0
     */
    public boolean isRepeat() {
        return this.count > 1;
    }
}
