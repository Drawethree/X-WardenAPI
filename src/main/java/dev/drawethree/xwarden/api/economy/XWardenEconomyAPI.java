package dev.drawethree.xwarden.api.economy;

import dev.drawethree.xwarden.api.MultiplierBreakdown;
import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The economy X-Warden is watching, and reversing what it should not have paid out.
 *
 * <p>Ask {@link #capabilities} before assuming a reading exists. X-Prison publishes an event for
 * every credit and debit before it is written; Vault publishes nothing at all and can only be
 * watched for balances changing. A check whose capability is absent is switched off with a line in
 * the console saying why, rather than run on inference - and an integrator should treat the absence
 * the same way.
 *
 * @since 1.0.0
 */
public interface XWardenEconomyAPI {

    /**
     * What {@link #bridgeName()} answers when no economy is being watched at all.
     *
     * @since 1.1.0
     */
    String NO_BRIDGE = "none";

    /**
     * Which economy is being watched.
     *
     * @return {@code X-Prison}, {@code Vault}, or {@link #NO_BRIDGE} when there is none. Decide
     *         what a reading means from {@link #capabilities()} rather than from this name
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    String bridgeName();

    /**
     * What the economy can be observed to do.
     *
     * @return the capabilities; empty when there is no economy
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Set<EconomyCapability> capabilities();

    /**
     * Whether one capability is present.
     *
     * @param capability the capability
     * @return whether the economy supports it
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean supports(EconomyCapability capability);

    /**
     * The currencies the economy knows.
     *
     * @return their names, as the economy spells them; empty when there is no economy
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<String> currencies();

    /**
     * A player's balance, from the economy itself.
     *
     * @param player   the player
     * @param currency which currency
     * @return the balance, or empty when there is no economy or it does not know that currency
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    Optional<BigDecimal> balanceOf(OfflinePlayer player, String currency);

    /**
     * The last payout measured in full for this player, broken down by the stage that multiplied
     * it.
     *
     * <p>Measured at four checkpoints rather than recomputed, because there is no canonical
     * resolver to recompute it from. Attribution inside the high band is lumped together, since
     * skins and quality share a priority - the breakdown says so rather than claiming a precision
     * it does not have.
     *
     * @param player the player
     * @return the last full measurement, or empty when none has been taken
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<MultiplierBreakdown> lastAudit(UUID player);

    /**
     * Measure this player's next payout in full, whatever the sampling rate.
     *
     * <p>The full audit is sampled by default because the payout event is the busiest in the
     * plugin. This turns sampling off for one payout.
     *
     * @param player the player
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void forceAudit(UUID player);

    /**
     * Whether rollback is switched on in {@code warden.yml} and the economy can adjust balances.
     *
     * @return whether {@link #applyRollback} would do anything
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean rollbackEnabled();

    /**
     * How far back a rollback may reach.
     *
     * @return the configured ceiling, in minutes
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    long rollbackMaxMinutes();

    /**
     * Works out what reversing this player's last so-many minutes would do, without doing any of
     * it.
     *
     * <p>Always plan before applying. The plan says which part came from exact minute-resolution
     * memory and which from hourly summaries, and it is the only argument
     * {@link #applyRollback} accepts - so nothing can reverse a balance without first having
     * worked out what it was reversing.
     *
     * @param player     the player
     * @param playerName their name, for the plan and the record
     * @param minutes    how far back to reach. {@code /xwarden rollback} refuses more than
     *                   {@link #rollbackMaxMinutes()}; this method leaves that to the caller
     * @return the plan, or {@code null} when the economy module is not running
     * @since 1.0.0
     */
    @Nullable
    @ThreadSafety(Requirement.OFF_PRIMARY)
    RollbackPlan planRollback(UUID player, String playerName, long minutes);

    /**
     * Applies a plan, and answers what it did, line by line.
     *
     * <p>On the server thread on purpose: a Vault economy is under no obligation to be thread-safe,
     * and this is the one path that moves somebody's money. It reverses balances only - anything
     * already bought with the money stays bought.
     *
     * @param plan the plan, from {@link #planRollback}
     * @return one line per currency reversed, or one line saying why nothing was
     * @throws IllegalArgumentException if the plan is {@code null}
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    List<String> applyRollback(@NotNull RollbackPlan plan);
}
