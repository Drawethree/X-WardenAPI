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
 */
public interface XWardenStaffAPI {

    /** Everything {@code /xwarden status} reports, as one reading. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    ServerStatus status();

    /**
     * Holds a player where they are.
     *
     * @param minutes how long for, or {@code 0} for a freeze with no end on it
     * @param actor   who did it, for the record
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean freeze(UUID player, long minutes, String actor);

    @ThreadSafety(Requirement.PRIMARY)
    boolean unfreeze(UUID player, String actor);

    @ThreadSafety(Requirement.ANY)
    boolean isFrozen(UUID player);

    /** Milliseconds left, or {@link Long#MAX_VALUE} for a freeze with no end on it. */
    @ThreadSafety(Requirement.ANY)
    long freezeRemaining(UUID player);

    @ThreadSafety(Requirement.ANY)
    Set<UUID> frozen();

    /**
     * Audits every payout this player receives, in full, for as long as it is on - rather than at
     * the sampling rate everybody else is measured at.
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean setWatched(UUID player, boolean watched);

    @ThreadSafety(Requirement.ANY)
    boolean isWatched(UUID player);

    @ThreadSafety(Requirement.ANY)
    Set<UUID> watched();

    /** What X-Warden knows about one player, online or not. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<PlayerSnapshot> snapshot(UUID player);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<PlayerSnapshot> online();

    /**
     * Players X-Warden has seen, newest first, optionally narrowed by name.
     *
     * <p>There is no player table: names are drawn from the findings, the income totals and the
     * session record together, so this answers for anybody X-Warden has ever had reason to write
     * down and nobody else.
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<PlayerSnapshot> players(String nameQuery, int offset, int limit);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    int countPlayers(String nameQuery);

    /** Current violation level for a module, after decay. */
    @ThreadSafety(Requirement.ANY)
    int violationLevel(UUID player, String moduleId);

    /**
     * Levels within one module, broken down by what they were about.
     *
     * <p>Counted per scope on purpose: somebody laundering one currency does not accumulate a level
     * against the money they earn honestly.
     */
    @ThreadSafety(Requirement.ANY)
    Map<String, Integer> levelsByScope(UUID player, String moduleId);

    /** Levels within one module, broken down by check. */
    @ThreadSafety(Requirement.ANY)
    Map<String, Double> breakdown(UUID player, String moduleId);

    /**
     * Throws away this player's violation levels.
     *
     * <p>Not reversible, and not the same as marking findings as handled: the levels are what let a
     * check escalate, so clearing them puts an ongoing problem back to the beginning. Ask first.
     */
    @ThreadSafety(Requirement.ANY)
    void resetLevels(UUID player, String playerName, String actor);

    /**
     * Whether the player is exempt from a module's checks, or from all of them.
     *
     * <p>Worth showing next to an empty report. An operator with an undeclared bypass node is
     * exempt from every check on the server, and the report staying empty looks exactly like
     * nothing being wrong.
     */
    @ThreadSafety(Requirement.ANY)
    boolean isBypassed(UUID player, String moduleId);

    /** Writes a plain-text case file on one player to disk, and answers where it went. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<String> exportCase(UUID player, String playerName, int days);

    /** The punishment presets in {@code warden-punishments.yml}, in file order. */
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
     * @param staff          who did it, as {@code Name (uuid)} or a plugin name
     * @param findingId      the finding this answers, or {@code 0}
     * @param reasonOverride replaces the preset's reason when not blank
     */
    @ThreadSafety(Requirement.PRIMARY)
    PunishmentResult punish(UUID player, String playerName, String presetId, String staff,
                            long findingId, String reasonOverride);

    /** Lifts a punishment early. False when it does not exist, is not liftable, or was lifted already. */
    @ThreadSafety(Requirement.PRIMARY)
    boolean lift(long punishmentId, String staff, String reason);

    /** Everything ever applied to a player, newest first. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Punishment> history(UUID player, int limit);

    /** The punishments binding a player right now. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Punishment> activePunishments(UUID player);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<Punishment> punishment(long punishmentId);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<StaffNote> notes(UUID player, int limit);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    StaffNote addNote(UUID player, String playerName, String note, String staff);

    /**
     * Installed plugins whose version is listed with a known exploit, as of the last scan.
     *
     * <p>Empty means nothing matched, or nothing has been scanned yet.
     */
    @ThreadSafety(Requirement.ANY)
    List<VulnerabilityMatch> vulnerabilities();
}
