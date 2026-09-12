package dev.drawethree.xwarden.api.automation;

import dev.drawethree.xwarden.api.AutomationBreakdown;
import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;
import org.jetbrains.annotations.Nullable;

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
 *
 * @since 1.0.0
 */
public interface XWardenAutomationAPI {

    /**
     * The player's current score.
     *
     * @param player the player
     * @return the score, or empty when too little mining has been recorded to produce one
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<AutomationScore> scoreOf(UUID player);

    /**
     * Scores the player now from whatever samples are held, rather than waiting for the next
     * analysis window.
     *
     * @param player the player
     * @return the score - {@link AutomationScore#isMeasured()} says whether there was enough to
     *         read - or {@code null} when the automation module is not running. Check
     *         {@link dev.drawethree.xwarden.api.module.XWardenModulesAPI#isEnabled} first if that
     *         matters
     * @since 1.0.0
     */
    @Nullable
    @ThreadSafety(Requirement.PRIMARY)
    AutomationScore scoreNow(UUID player);

    /**
     * The same score in the simplified shape the original API exposes.
     *
     * @param player the player
     * @return the breakdown, or empty when there is no score
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<AutomationBreakdown> breakdownOf(UUID player);

    /**
     * Every player currently holding a measured score.
     *
     * @return player to score, as of now
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Map<UUID, AutomationScore> liveScores();

    /**
     * Sends a sustained high scorer a code to type back.
     *
     * <p>Not answering is evidence nobody had to accuse anybody to obtain. It ships switched off,
     * because a player genuinely away from their keyboard fails it in exactly the way a macro does.
     *
     * @param player         the player
     * @param timeoutSeconds how long they have to answer
     * @return {@code false} when the player is not online, or already has one pending
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean issueChallenge(UUID player, int timeoutSeconds);

    /**
     * Whether the player has a challenge they have not yet answered.
     *
     * @param player the player
     * @return whether one is pending
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean isChallengePending(UUID player);

    /**
     * Withdraws a pending challenge without counting it as failed.
     *
     * @param player the player
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void cancelChallenge(UUID player);

    /**
     * Starts measuring the population, so the baselines are what normal looks like on this server
     * rather than a guess.
     *
     * <p>A run already in progress is replaced; read {@link #suggestions} first if it has anything
     * worth keeping.
     *
     * @param durationMillis how long to measure for
     * @return {@code false} when the automation module is not running
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean startCalibration(long durationMillis);

    /**
     * Discards the current run. {@link #calibration()} is empty afterwards, so read
     * {@link #suggestions} first if it has anything worth keeping.
     *
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    void stopCalibration();

    /**
     * The current run, finished or not.
     *
     * @return its status, or empty when none has been started since the server came up, or the
     *         last one was stopped
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<CalibrationStatus> calibration();

    /**
     * What calibration would change, if it has measured enough people to be worth acting on.
     *
     * @param minPlayers how many different people a baseline needs before it is suggested
     * @param minSamples how many readings it needs
     * @return one suggestion per baseline that meets both floors; empty otherwise
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<BaselineSuggestion> suggestions(int minPlayers, int minSamples);

    /**
     * Writes those suggestions into {@code warden.yml} as the new baselines, one line each, and
     * re-reads the file so they take effect.
     *
     * @param suggestions what to write, from {@link #suggestions}
     * @return whether every baseline was written
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean applySuggestions(List<BaselineSuggestion> suggestions);
}
