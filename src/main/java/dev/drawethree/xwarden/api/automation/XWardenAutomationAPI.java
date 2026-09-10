package dev.drawethree.xwarden.api.automation;

import dev.drawethree.xwarden.api.AutomationBreakdown;
import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * How much like a machine somebody is mining.
 *
 * <p>Speed is not the signal; consistency is. The score combines timing variation, timing spread,
 * repeating loops, camera drift, session continuity and movement repetition, and every signal
 * carries what an ordinary player looks like on the same measurement. "0.4% variation" means
 * nothing on its own; "0.4%, and people are usually above 15%" is something a moderator can act on.
 *
 * <p>A score is a reading, never a verdict. Two things follow from that and both matter: an absent
 * score is not a zero - it means not enough mining has been recorded to say anything - and a high
 * score is a reason to look, not a reason to punish.
 */
public interface XWardenAutomationAPI {

    /**
     * The player's current score, or empty when too little mining has been recorded to produce one.
     */
    @ThreadSafety(Requirement.ANY)
    Optional<AutomationScore> scoreOf(UUID player);

    /** Scores them now from whatever samples are held, rather than waiting for the next window. */
    @ThreadSafety(Requirement.PRIMARY)
    AutomationScore scoreNow(UUID player);

    /** The same score in the simplified shape the older API exposes. */
    @ThreadSafety(Requirement.ANY)
    Optional<AutomationBreakdown> breakdownOf(UUID player);

    /** Every player currently holding a measured score. */
    @ThreadSafety(Requirement.ANY)
    Map<UUID, AutomationScore> liveScores();

    /**
     * Sends a sustained high scorer a code to type back.
     *
     * <p>Not answering is evidence nobody had to accuse anybody to obtain. It ships switched off,
     * because a player genuinely away from their keyboard fails it in exactly the way a macro does.
     *
     * @return false when the player is not online, or already has one pending
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean issueChallenge(UUID player, int timeoutSeconds);

    @ThreadSafety(Requirement.ANY)
    boolean isChallengePending(UUID player);

    @ThreadSafety(Requirement.ANY)
    void cancelChallenge(UUID player);

    /**
     * Starts measuring the population, so the baselines are what normal looks like on this server
     * rather than a guess.
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean startCalibration(long durationMillis);

    @ThreadSafety(Requirement.PRIMARY)
    void stopCalibration();

    @ThreadSafety(Requirement.ANY)
    Optional<CalibrationStatus> calibration();

    /** What calibration would change, if it has measured enough people to be worth acting on. */
    @ThreadSafety(Requirement.ANY)
    List<BaselineSuggestion> suggestions(int minPlayers, int minSamples);

    /** Writes those suggestions into the config as the new baselines. */
    @ThreadSafety(Requirement.PRIMARY)
    boolean applySuggestions(List<BaselineSuggestion> suggestions);
}
