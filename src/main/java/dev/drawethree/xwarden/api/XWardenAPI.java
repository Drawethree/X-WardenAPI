package dev.drawethree.xwarden.api;

import dev.drawethree.xwarden.api.addon.XWardenAddonInfo;
import dev.drawethree.xwarden.api.automation.XWardenAutomationAPI;
import dev.drawethree.xwarden.api.config.XWardenConfigAPI;
import dev.drawethree.xwarden.api.economy.XWardenEconomyAPI;
import dev.drawethree.xwarden.api.economy.XWardenLedgerAPI;
import dev.drawethree.xwarden.api.flags.XWardenFlagsAPI;
import dev.drawethree.xwarden.api.integrity.XWardenIntegrityAPI;
import dev.drawethree.xwarden.api.module.XWardenModulesAPI;
import dev.drawethree.xwarden.api.network.XWardenNetworkAPI;
import dev.drawethree.xwarden.api.staff.XWardenStaffAPI;
import dev.drawethree.xwarden.api.flags.Violation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The public surface of X-Warden, so that any plugin can report suspicious activity into the same
 * violation pipeline and read back what Warden already knows.
 * <p>
 * Compile against X-Warden at {@code provided} scope and guard your usage, because the addon may not
 * be installed:
 * <pre>{@code
 * try {
 *     XWardenAPI warden = XWardenAPI.getInstance();
 *     warden.registerCheck("economy", "mygame-impossible-win");
 *     warden.report(uuid, "economy", "mygame-impossible-win", 80, Map.of("Wagered", "1000"));
 * } catch (IllegalStateException | NoClassDefFoundError wardenAbsent) {
 *     // carry on without it
 * }
 * }</pre>
 */
public interface XWardenAPI {

    /**
     * Returns the running instance.
     *
     * @throws IllegalStateException if X-Warden is not installed, not enabled, or still starting
     */
    static XWardenAPI getInstance() {
        return InstanceHolder.require();
    }

    /**
     * Returns the running instance, or empty when X-Warden is not available.
     */
    static Optional<XWardenAPI> find() {
        return Optional.ofNullable(InstanceHolder.get());
    }

    /**
     * Registers a check identifier your addon reports into, so Warden gives it real settings
     * instead of discarding its findings.
     * <p>
     * Call this once, on enable, before your first {@link #report}. The check picks up whatever the
     * owner has written under {@code modules.<module>.checks.<check>} in {@code warden.yml}, and
     * Warden's ordinary defaults - enabled, and ALERT - where they have written nothing. An addon
     * therefore cannot ship a check that punishes on its own; only the owner can raise it.
     * <p>
     * The registration lasts as long as Warden is running and survives {@code /xwarden reload}.
     *
     * @param moduleId one of Warden's own modules: {@code economy}, {@code integrity},
     *                 {@code automation} or {@code network}
     * @param checkId  your identifier. Keep it lowercase and hyphenated, and distinctive enough not
     *                 to collide with another addon's
     * @return {@code false} if the module id is not one Warden knows, in which case nothing you
     *         report under it will ever be raised
     */
    boolean registerCheck(String moduleId, String checkId);

    /**
     * Reports a player for something your own addon detected.
     * <p>
     * The check identifier must be one of Warden's own or one you passed to
     * {@link #registerCheck}, otherwise the report is discarded. Reports for a player who holds a
     * bypass permission are discarded as well, as are ones a plugin cancels through
     * {@code WardenViolationEvent}.
     *
     * @param player     who to report
     * @param moduleId   the module the check belongs to
     * @param checkId    the check identifier, as it appears in the configuration
     * @param confidence 0 - 100; how sure you are, not how bad it is
     * @param evidence   the numbers behind the report, shown to staff exactly as given
     * @return the violation that was raised, or empty if it was discarded
     */
    Optional<Violation> report(UUID player, String moduleId, String checkId, int confidence,
                               Map<String, String> evidence);

    /**
     * Reports a defect rather than a player: a malformed amount, an impossible state, a broken
     * assumption. This reaches staff as a bug notice and never touches anybody's violation level.
     *
     * @param source where the problem was noticed, for example {@code "mycasino/payout"}
     * @param detail what went wrong, in plain language
     */
    void reportBug(String source, String detail);

    /**
     * The player's current automation score, 0 - 100, or 0 when it has not been measured.
     */
    int getAutomationScore(UUID player);

    /**
     * The player's automation score with its per-signal breakdown, when one has been measured.
     */
    Optional<AutomationBreakdown> getAutomationBreakdown(UUID player);

    /**
     * The player's current violation level for a module, after decay.
     */
    int getViolationLevel(UUID player, String moduleId);

    /**
     * The player's most recent violations, newest first.
     */
    List<Violation> getRecentViolations(UUID player, int limit);

    /**
     * Whether the player holds a bypass permission, globally or for that module.
     */
    boolean isTrusted(UUID player, String moduleId);

    /**
     * Writes a transaction into Warden's ledger so it appears in reports and can be rolled back.
     * Use this for money your own addon moves outside the core currency API.
     *
     * @param amount        positive to credit the player, negative to debit them
     * @param source        your addon name
     * @param correlationId shared between both halves of a transfer, or null
     */
    void recordTransaction(UUID player, String currency, BigDecimal amount, String source,
                           String correlationId);

    /**
     * Returns the last payout Warden measured for this player, broken down by stage, and marks the
     * next one to be measured in full regardless of the sampling rate.
     * <p>
     * The first call after a quiet period is normally empty: there is nothing to report until the
     * player is paid again.
     */
    Optional<MultiplierBreakdown> auditMultipliers(Player player, String currency);

    /**
     * Stamps an item so duplicates of it can be proven. Pickaxes already carry an identity of their
     * own and are returned unchanged.
     *
     * @return the identifier now on the item, or empty if it cannot carry one
     */
    Optional<String> fingerprint(ItemStack item, String itemClass);

    /**
     * Reads the identifier on an item, if it has one.
     */
    Optional<String> getFingerprint(ItemStack item);

    /**
     * Describes one item your container holds, ready to be handed back from
     * {@link ContainerSource#snapshot()}.
     * <p>
     * Reading an identity touches the item's metadata, so call this on the server thread while the
     * items are in hand, keep what it gives you, and let {@code snapshot()} return the kept copies.
     * Working this out yourself is not equivalent: a prison pickaxe carries an identity of its own
     * and everything else carries Warden's, and only Warden knows which is which.
     *
     * @return the record, or empty when the item carries no identity and so is not tracked
     */
    Optional<HeldItem> describeStored(ItemStack item, UUID holder, String holderName,
                                      String container, int slot);

    /**
     * Registers a container your addon owns so the items inside it take part in duplicate
     * detection. Without this, an item hidden in your storage can never be reported as a duplicate.
     */
    void registerContainerSource(ContainerSource source);

    /**
     * Stops including a container in duplicate detection.
     */
    void unregisterContainerSource(ContainerSource source);

    /**
     * The identifiers of every module Warden knows about.
     */
    Set<String> getModuleIds();

    /**
     * Whether a module is switched on and running.
     */
    boolean isModuleEnabled(String moduleId);


    /**
     * How many methods this API has grown by since it was first published.
     *
     * <p>Check it before calling anything added after the version you built against. An addon
     * compiled against a newer X-Warden and run on an older one hits {@code NoSuchMethodError} at
     * the call site, which is a stack trace nobody can act on; refusing politely against a number
     * is better for everybody.
     */
    int apiVersion();

    /** The findings X-Warden has recorded, and marking them as dealt with. */
    XWardenFlagsAPI getFlagsApi();

    /** Where the money went. */
    XWardenLedgerAPI getLedgerApi();

    /** The economy being watched, and reversing what it should not have paid out. */
    XWardenEconomyAPI getEconomyApi();

    /** Duplicated items, proven rather than suspected. */
    XWardenIntegrityAPI getIntegrityApi();

    /** Accounts that look like the same hands. Read-only; there is no action path here. */
    XWardenNetworkAPI getNetworkApi();

    /** How much like a machine somebody is mining. */
    XWardenAutomationAPI getAutomationApi();

    /** The four modules, the checks inside them, and whether those checks can actually fire. */
    XWardenModulesAPI getModulesApi();

    /** What a staff member can do, and what the server currently looks like. */
    XWardenStaffAPI getStaffApi();

    /** Reading and changing X-Warden's configuration. */
    XWardenConfigAPI getConfigApi();

    /** Every addon X-Warden has loaded. */
    List<XWardenAddonInfo> getLoadedAddons();

    /** @return false if there is no addon by that name, or it is already running */
    boolean enableAddon(String name);

    boolean disableAddon(String name);

    /**
     * Loads an addon jar that is already in the addons folder, without a restart.
     *
     * <p>Refused if an addon by that name is already loaded. Call {@link #unloadAddon(String)}
     * first to replace a running one.
     *
     * @param filename the jar's file name; it must sit directly in {@code plugins/X-Warden/addons/}
     */
    boolean loadAddonFromFile(String filename);

    /**
     * Stops an addon and lets go of its jar, so the file can be replaced and loaded again.
     *
     * <p>Stronger than {@link #disableAddon(String)}, which only calls {@code onDisable} and leaves
     * the addon loaded against the same classloader - so a disabled addon re-enabled after its jar
     * changed is still running the code it started with. This closes the classloader, which is what
     * makes a new version of the jar readable.
     *
     * <p>The addon is gone from {@link #getLoadedAddons()} afterwards and only
     * {@link #loadAddonFromFile(String)} brings it back.
     *
     * @return false if no addon by that name is loaded
     */
    boolean unloadAddon(String name);

    /**
     * The jars sitting in {@code plugins/X-Warden/addons/}, loaded or not.
     *
     * <p>What {@link #loadAddonFromFile(String)} will accept. A jar's file name is rarely the
     * addon's name - {@code X-Warden-Dashboard-1.0.0.jar} holds an addon called {@code Dashboard} -
     * so anything offering a choice of jars has to read the folder rather than the loaded list.
     */
    List<String> addonFileNames();

    /**
     * Registers where a web panel addon can be reached, so {@code /xwarden} can tell staff. Pass
     * null on disable.
     */
    void setDashboardUrl(String url);

    /** The URL a panel addon registered, or null if none is running. */
    String getDashboardUrl();

    /**
     * Holds the running instance. Set by the addon on enable and cleared on disable.
     */
    final class InstanceHolder {

        private static volatile XWardenAPI instance;

        private InstanceHolder() {
            throw new UnsupportedOperationException("Cannot instantiate");
        }

        /**
         * Publishes the running instance. Warden calls this itself.
         *
         * <p>Clearing it is always allowed, because that is how Warden shuts down. Replacing a live
         * instance is refused: this field is what every integrator resolves Warden through, and a
         * plugin that overwrote it - by accident or otherwise - would silently redirect all of them.
         *
         * @throws IllegalStateException if an instance is already published
         */
        public static void set(XWardenAPI api) {
            if (api != null && instance != null) {
                throw new IllegalStateException("X-Warden is already running; its API instance"
                        + " cannot be replaced while it is.");
            }
            instance = api;
        }

        static XWardenAPI get() {
            return instance;
        }

        static XWardenAPI require() {
            XWardenAPI current = instance;
            if (current == null) {
                throw new IllegalStateException("X-Warden is not available");
            }
            return current;
        }
    }
}
