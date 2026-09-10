package dev.drawethree.xwarden.api.flags;

import java.util.UUID;

/**
 * Every open finding of one check on one player, counted as a single ongoing problem.
 *
 * @param latestId the newest finding in the group, which is the one whose evidence is shown
 * @param topConfidence the highest confidence any finding in the group reached
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

    public static FlagGroup ofSingle(dev.drawethree.xwarden.api.flags.Violation flag) {
        return new FlagGroup(flag.player(), flag.playerName(), flag.moduleId(), flag.checkId(),
                flag.scope(), 1, flag.confidence(), flag.timestamp(), flag.timestamp(), flag.id());
    }

    public boolean isRepeat() {
        return this.count > 1;
    }
}
