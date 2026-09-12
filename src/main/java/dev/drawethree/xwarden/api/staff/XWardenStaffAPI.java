package dev.drawethree.xwarden.api.staff;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;
import dev.drawethree.xwarden.api.staff.punish.Punishment;
import dev.drawethree.xwarden.api.staff.punish.PunishmentPreset;
import dev.drawethree.xwarden.api.staff.punish.PunishmentResult;
import dev.drawethree.xwarden.api.staff.punish.StaffNote;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * What a staff member can do, and what the server currently looks like.
 *
 * <p>Everything here is something a human decided to do. X-Warden holds a player still so somebody
 * can talk to them; it does not decide who deserves it.
 *
 * @since 1.0.0
 */
public interface XWardenStaffAPI {

    /**
     * Everything {@code /xwarden status} reports, as one reading.
     *
     * @return the status
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    ServerStatus status();

    /**
     * Holds a player where they are.
     *
     * <p>A player who is already frozen has their duration replaced by this one.
     *
     * @param player  the player
     * @param minutes how long for, or {@code 0} for a freeze with no end on it
     * @param actor   who did it, for the record
     * @return {@code false} if they were already frozen
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean freeze(UUID player, long minutes, String actor);

    /**
     * Lets a frozen player move again.
     *
     * @param player the player
     * @param actor  who did it, for the record
     * @return {@code false} if they were not frozen
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean unfreeze(UUID player, String actor);

    /**
     * Whether a player is being held still.
     *
     * @param player the player
     * @return whether they are frozen now
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean isFrozen(UUID player);

    /**
     * How long a freeze has left.
     *
     * @param player the player
     * @return milliseconds left, {@link Long#MAX_VALUE} for a freeze with no end on it, or
     *         {@code 0} when they are not frozen
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    long freezeRemaining(UUID player);

    /**
     * Everybody currently frozen.
     *
     * @return their ids
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Set<UUID> frozen();

    /**
     * Audits every payout this player receives, in full, for as long as it is on - rather than at
     * the sampling rate everybody else is measured at.
     *
     * @param player  the player
     * @param watched whether to watch them
     * @return {@code false} when the player is not online, since the flag lives on the live profile
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean setWatched(UUID player, boolean watched);

    /**
     * Whether a player's payouts are being audited in full.
     *
     * @param player the player
     * @return whether they are watched
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean isWatched(UUID player);

    /**
     * Everybody currently watched.
     *
     * @return their ids
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Set<UUID> watched();

    /**
     * What X-Warden knows about one player, online or not.
     *
     * @param player the player
     * @return the snapshot, or empty when X-Warden has never had reason to write them down
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<PlayerSnapshot> snapshot(UUID player);

    /**
     * A snapshot of everybody online.
     *
     * @return one per online player
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<PlayerSnapshot> online();

    /**
     * Players X-Warden has seen, newest first, optionally narrowed by name.
     *
     * <p>There is no player table: names are drawn from the findings, the income totals and the
     * session record together, so this answers for anybody X-Warden has ever had reason to write
     * down and nobody else.
     *
     * @param nameQuery part of a name, or blank for everybody
     * @param offset    how many to skip
     * @param limit     the most to return
     * @return the page
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<PlayerSnapshot> players(String nameQuery, int offset, int limit);

    /**
     * How many players {@link #players} would match in all.
     *
     * @param nameQuery part of a name, or blank for everybody
     * @return the count
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countPlayers(String nameQuery);

    /**
     * Current violation level for a module, after decay.
     *
     * @param player   the player
     * @param moduleId the module
     * @return the level, rounded down
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    int violationLevel(UUID player, String moduleId);

    /**
     * Levels within one module, broken down by what they were about.
     *
     * <p>Counted per scope on purpose: somebody laundering one currency does not accumulate a level
     * against the money they earn honestly.
     *
     * @param player   the player
     * @param moduleId the module
     * @return scope to level, rounded down
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Map<String, Integer> levelsByScope(UUID player, String moduleId);

    /**
     * Levels within one module, broken down by check.
     *
     * @param player   the player
     * @param moduleId the module
     * @return scoped check id to level, exact
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Map<String, Double> breakdown(UUID player, String moduleId);

    /**
     * Throws away this player's violation levels.
     *
     * <p>Not reversible, and not the same as marking findings as handled: the levels are what let a
     * check escalate, so clearing them puts an ongoing problem back to the beginning. Ask first.
     *
     * @param player     the player
     * @param playerName their name, for the record
     * @param actor      who did it, for the record
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void resetLevels(UUID player, String playerName, String actor);

    /**
     * Whether the player is exempt from a module's checks, or from all of them.
     *
     * <p>Worth showing next to an empty report. An operator with an undeclared bypass node is
     * exempt from every check on the server, and the report staying empty looks exactly like
     * nothing being wrong.
     *
     * @param player   the player
     * @param moduleId the module
     * @return whether they hold the module's bypass permission or the global one
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean isBypassed(UUID player, String moduleId);

    /**
     * Writes a plain-text case file on one player to disk.
     *
     * @param player     the player
     * @param playerName their name, for the file
     * @param days       how far back to reach
     * @return where it went, or empty when it could not be written
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<String> exportCase(UUID player, String playerName, int days);

    /**
     * The punishment presets in {@code warden-punishments.yml}, in file order.
     *
     * @return the presets, whether or not the punishment system is switched on
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.ANY)
    List<PunishmentPreset> presets();

    /**
     * Applies a preset to a player, taking the next step of its ladder.
     *
     * <p>The step is chosen from how often this preset (or, if so configured, this type) has been
     * applied to the player inside the escalation window. A configured command template is
     * dispatched as the console; an empty template means X-Warden enforces the step itself. Either
     * way the record is written first, so the history is complete even when the command failed.
     *
     * @param player         the player
     * @param playerName     their name
     * @param presetId       the preset, from {@link #presets()}
     * @param staff          who did it, as {@code Name (uuid)} or a plugin name
     * @param findingId      the finding this answers, or {@code 0}
     * @param reasonOverride replaces the preset's reason when not blank
     * @return what happened, and the record if it was applied
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    PunishmentResult punish(UUID player, String playerName, String presetId, String staff,
                            long findingId, String reasonOverride);

    /**
     * Lifts a punishment early.
     *
     * @param punishmentId the record
     * @param staff        who did it
     * @param reason       why, for the record
     * @return {@code false} when it does not exist, is not liftable, or was lifted already
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean lift(long punishmentId, String staff, String reason);

    /**
     * Everything ever applied to a player, newest first.
     *
     * @param player the player
     * @param limit  the most to return
     * @return the records, newest first
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Punishment> history(UUID player, int limit);

    /**
     * The punishments binding a player right now.
     *
     * @param player the player
     * @return timed or permanent punishments that are neither lifted nor expired
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Punishment> activePunishments(UUID player);

    /**
     * One punishment by its id.
     *
     * @param punishmentId the record
     * @return the punishment, or empty
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<Punishment> punishment(long punishmentId);

    /**
     * What staff have written about a player, newest first.
     *
     * @param player the player
     * @param limit  the most to return
     * @return the notes, newest first
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<StaffNote> notes(UUID player, int limit);

    /**
     * Writes a note about a player, kept with the punishment history.
     *
     * @param player     the player
     * @param playerName their name
     * @param note       the text
     * @param staff      who wrote it, as {@code Name (uuid)} or a plugin name
     * @return the note as stored
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    StaffNote addNote(UUID player, String playerName, String note, String staff);

    /**
     * Installed plugins whose version is listed with a known exploit, as of the last scan.
     *
     * <p>Empty means nothing matched, or nothing has been scanned yet.
     *
     * @return the matches
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.ANY)
    List<VulnerabilityMatch> vulnerabilities();
}
