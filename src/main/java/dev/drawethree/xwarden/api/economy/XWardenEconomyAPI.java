package dev.drawethree.xwarden.api.economy;

import dev.drawethree.xwarden.api.MultiplierBreakdown;
import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;
import org.bukkit.OfflinePlayer;

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
 */
public interface XWardenEconomyAPI {

    @ThreadSafety(Requirement.ANY)
    String bridgeName();

    @ThreadSafety(Requirement.ANY)
    Set<EconomyCapability> capabilities();

    @ThreadSafety(Requirement.ANY)
    boolean supports(EconomyCapability capability);

    @ThreadSafety(Requirement.ANY)
    List<String> currencies();

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
     */
    @ThreadSafety(Requirement.ANY)
    Optional<MultiplierBreakdown> lastAudit(UUID player);

    /**
     * Measure this player's next payout in full, whatever the sampling rate.
     *
     * <p>The full audit is sampled by default because the payout event is the busiest in the
     * plugin. This turns sampling off for one payout.
     */
    @ThreadSafety(Requirement.ANY)
    void forceAudit(UUID player);

    @ThreadSafety(Requirement.ANY)
    boolean rollbackEnabled();

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
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    RollbackPlan planRollback(UUID player, String playerName, long minutes);

    /**
     * Applies a plan, and answers what it did, line by line.
     *
     * <p>On the server thread on purpose: a Vault economy is under no obligation to be thread-safe,
     * and this is the one path that moves somebody's money. It reverses balances only - anything
     * already bought with the money stays bought.
     */
    @ThreadSafety(Requirement.PRIMARY)
    List<String> applyRollback(RollbackPlan plan);
}
