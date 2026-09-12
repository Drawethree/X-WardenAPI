package dev.drawethree.xwarden.api.staff;

import dev.drawethree.xwarden.api.economy.EconomyCapability;

import java.util.Set;

/**
 * Everything {@code /xwarden status} reports, as one reading.
 *
 * <p>The command and any panel both build their answer from this, so the two cannot come to differ.
 *
 * @param wardenVersion       the running X-Warden version
 * @param platformName        which platform was resolved - X-Prison's, or standalone
 * @param hooked              whether X-Warden is sharing X-Prison's database or running its own
 * @param storage             which database, in words
 * @param economyName         where money movements come from, or a note that none is available
 * @param capabilities        what that economy can be observed to do
 * @param missing             what it cannot. The checks that need those stay off rather than guess
 * @param modulesRunning      how many modules started
 * @param modulesTotal        how many there are
 * @param preset              the preset {@code warden.yml} names
 * @param openFlags           findings nobody has marked as handled
 * @param flagsLastDay        findings raised in the last twenty-four hours
 * @param staffHearingAlerts  how many staff online have alerts switched on. Zero means findings are
 *                            being recorded and nobody is being told
 * @param lastSweepAt         when the last duplicate sweep finished, or {@code 0} for never
 * @param ledgerQueueDepth    rows waiting to be written. A number that only grows is a storage
 *                            problem showing itself before anything else notices
 * @param incomeWindowEntries how much minute-resolution mining income is held in memory
 * @param calibrating         whether a calibration run is in progress
 * @param onlinePlayers       how many players are online
 * @param frozenPlayers       how many of them are being held still
 * @param tps                 ticks per second, counted rather than asked for, or {@code null} where
 *                            nothing has measured it - the tick clock only runs while the integrity
 *                            module does. Absent is not the same as zero
 * @since 1.0.0
 */
public record ServerStatus(String wardenVersion,
                           String platformName,
                           boolean hooked,
                           String storage,
                           String economyName,
                           Set<EconomyCapability> capabilities,
                           Set<EconomyCapability> missing,
                           int modulesRunning,
                           int modulesTotal,
                           String preset,
                           int openFlags,
                           int flagsLastDay,
                           int staffHearingAlerts,
                           long lastSweepAt,
                           int ledgerQueueDepth,
                           int incomeWindowEntries,
                           boolean calibrating,
                           int onlinePlayers,
                           int frozenPlayers,
                           Double tps) {

    /**
     * Whether any economy is being watched.
     *
     * @return whether {@link #capabilities()} is non-empty
     * @since 1.0.0
     */
    public boolean hasEconomy() {
        return !this.capabilities.isEmpty();
    }

    /**
     * Whether a tick rate is available.
     *
     * @return whether {@link #tps()} is set
     * @since 1.0.0
     */
    public boolean tpsMeasured() {
        return this.tps != null;
    }
}
