package dev.drawethree.xwarden.api;

import dev.drawethree.xwarden.api.addon.XWardenAddonInfo;
import dev.drawethree.xwarden.api.automation.XWardenAutomationAPI;
import dev.drawethree.xwarden.api.client.XWardenClientAPI;
import dev.drawethree.xwarden.api.config.XWardenConfigAPI;
import dev.drawethree.xwarden.api.economy.XWardenEconomyAPI;
import dev.drawethree.xwarden.api.economy.XWardenLedgerAPI;
import dev.drawethree.xwarden.api.flags.Violation;
import dev.drawethree.xwarden.api.flags.XWardenFlagsAPI;
import dev.drawethree.xwarden.api.integrity.XWardenIntegrityAPI;
import dev.drawethree.xwarden.api.module.XWardenModulesAPI;
import dev.drawethree.xwarden.api.network.XWardenNetworkAPI;
import dev.drawethree.xwarden.api.staff.XWardenStaffAPI;
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
 *
 * <p>Compile against this artifact at {@code provided} scope and guard your usage, because X-Warden
 * may not be installed on the server your plugin lands on:
 * <pre>{@code
 * try {
 *     XWardenAPI warden = XWardenAPI.getInstance();
 *     warden.registerCheck("economy", "mygame-impossible-win");
 *     warden.report(uuid, "economy", "mygame-impossible-win", 80, Map.of("Wagered", "1000"));
 * } catch (IllegalStateException | NoClassDefFoundError wardenAbsent) {
 *     // carry on without it
 * }
 * }</pre>
 *
 * <p>The instance is also registered with Bukkit's {@code ServicesManager} under this interface, so
 * {@code Bukkit.getServicesManager().load(XWardenAPI.class)} is an equivalent way in.
 *
 * <p>The methods declared directly on this interface are the original, simplified surface. Everything
 * added since lives on the area APIs - {@link #getFlagsApi()}, {@link #getStaffApi()} and the rest -
 * each of which declares, per method, which thread it may be called from (see {@link ThreadSafety}).
 *
 * @since 1.0.0
 */
public interface XWardenAPI {

    /**
     * Returns the running instance.
     *
     * @return the running instance
     * @throws IllegalStateException if X-Warden is not installed, not enabled, or still starting
     * @since 1.0.0
     */
    static XWardenAPI getInstance() {
        return InstanceHolder.require();
    }

    /**
     * Returns the running instance, or empty when X-Warden is not available.
     *
     * @return the running instance, or empty
     * @since 1.0.0
     */
    static Optional<XWardenAPI> find() {
        return Optional.ofNullable(InstanceHolder.get());
    }

    /**
     * Registers a check identifier your plugin reports into, so Warden gives it real settings
     * instead of discarding its findings.
     *
     * <p>Call this once, on enable, before your first {@link #report}. The check picks up whatever the
     * owner has written under {@code modules.<module>.checks.<check>} in {@code warden.yml}, and
     * Warden's ordinary defaults - enabled, and ALERT - where they have written nothing. A plugin
     * therefore cannot ship a check that punishes on its own; only the owner can raise it.
     *
     * <p>The registration lasts as long as Warden is running and survives {@code /xwarden reload}.
     *
     * @param moduleId one of Warden's own modules: {@code economy}, {@code integrity},
     *                 {@code automation}, {@code network} or {@code client}
     * @param checkId  your identifier. Keep it lowercase and hyphenated, and distinctive enough not
     *                 to collide with another plugin's
     * @return {@code false} if the module id is not one Warden knows, in which case nothing you
     *         report under it will ever be raised
     * @since 1.0.0
     */
    boolean registerCheck(String moduleId, String checkId);

    /**
     * Reports a player for something your own plugin detected.
     *
     * <p>The check identifier must be one of Warden's own or one you passed to
     * {@link #registerCheck}, otherwise the report is discarded. Reports for a player who holds a
     * bypass permission are discarded as well, as are ones a plugin cancels through
     * {@link dev.drawethree.xwarden.api.event.WardenViolationEvent}.
     *
     * @param player     who to report
     * @param moduleId   the module the check belongs to
     * @param checkId    the check identifier, as it appears in the configuration
     * @param confidence 0 - 100; how sure you are, not how bad it is
     * @param evidence   the numbers behind the report, shown to staff exactly as given
     * @return the violation that was raised, or empty if it was discarded
     * @since 1.0.0
     */
    Optional<Violation> report(UUID player, String moduleId, String checkId, int confidence,
                               Map<String, String> evidence);

    /**
     * Reports a defect rather than a player: a malformed amount, an impossible state, a broken
     * assumption. This reaches staff as a bug notice and never touches anybody's violation level.
     *
     * @param source where the problem was noticed, for example {@code "mycasino/payout"}
     * @param detail what went wrong, in plain language
     * @since 1.0.0
     */
    void reportBug(String source, String detail);

    /**
     * The player's current automation score.
     *
     * @param player the player
     * @return the score, 0 - 100, or {@code 0} when it has not been measured. Absent is not the
     *         same as zero; use {@link #getAutomationBreakdown} to tell the two apart
     * @since 1.0.0
     */
    int getAutomationScore(UUID player);

    /**
     * The player's automation score with its per-signal breakdown, when one has been measured.
     *
     * @param player the player
     * @return the breakdown, or empty when not enough mining has been recorded to score them
     * @since 1.0.0
     */
    Optional<AutomationBreakdown> getAutomationBreakdown(UUID player);

    /**
     * The player's current violation level for a module, after decay.
     *
     * @param player   the player
     * @param moduleId the module
     * @return the level, rounded down, or {@code 0} when there is none
     * @since 1.0.0
     */
    int getViolationLevel(UUID player, String moduleId);

    /**
     * The player's most recent violations, newest first.
     *
     * @param player the player
     * @param limit  the most to return
     * @return the violations, newest first; empty when there are none
     * @since 1.0.0
     */
    List<Violation> getRecentViolations(UUID player, int limit);

    /**
     * Whether the player holds a bypass permission, globally or for that module.
     *
     * @param player   the player
     * @param moduleId the module
     * @return whether the player is exempt from that module's checks
     * @since 1.0.0
     */
    boolean isTrusted(UUID player, String moduleId);

    /**
     * Writes a transaction into Warden's ledger so it appears in reports and can be rolled back.
     * Use this for money your own plugin moves outside the economy Warden is watching.
     *
     * @param player        whose balance moved
     * @param currency      which currency
     * @param amount        positive to credit the player, negative to debit them
     * @param source        your plugin's name
     * @param correlationId shared between both halves of a transfer, or {@code null}
     * @since 1.0.0
     */
    void recordTransaction(UUID player, String currency, BigDecimal amount, String source,
                           String correlationId);

    /**
     * Returns the last payout Warden measured for this player, broken down by stage, and marks the
     * next one to be measured in full regardless of the sampling rate.
     *
     * <p>The first call after a quiet period is normally empty: there is nothing to report until the
     * player is paid again.
     *
     * @param player   the player
     * @param currency which currency
     * @return the last measured payout, or empty when none has been measured yet
     * @since 1.0.0
     */
    Optional<MultiplierBreakdown> auditMultipliers(Player player, String currency);

    /**
     * Stamps an item so duplicates of it can be proven. Prison pickaxes already carry an identity of
     * their own and are returned unchanged.
     *
     * @param item      the item, modified in place
     * @param itemClass the class to record it under, such as {@code CUSTOM}
     * @return the identifier now on the item, or empty if it cannot carry one
     * @since 1.0.0
     */
    Optional<String> fingerprint(ItemStack item, String itemClass);

    /**
     * Reads the identifier on an item, if it has one.
     *
     * @param item the item
     * @return its identity, or empty when it carries none
     * @since 1.0.0
     */
    Optional<String> getFingerprint(ItemStack item);

    /**
     * Describes one item your container holds, ready to be handed back from
     * {@link ContainerSource#snapshot()}.
     *
     * <p>Reading an identity touches the item's metadata, so call this on the server thread while the
     * items are in hand, keep what it gives you, and let {@code snapshot()} return the kept copies.
     * Working this out yourself is not equivalent: a prison pickaxe carries an identity of its own
     * and everything else carries Warden's, and only Warden knows which is which.
     *
     * @param item       the item
     * @param holder     whose storage it is in, or {@code null} for a container with no owner
     * @param holderName that holder's name, as it should read to staff
     * @param container  your container's id, as passed to {@link ContainerSource#id()}
     * @param slot       the slot it sits in, or {@code -1} where the container has no slots
     * @return the record, or empty when the item carries no identity and so is not tracked
     * @since 1.0.0
     */
    Optional<HeldItem> describeStored(ItemStack item, UUID holder, String holderName,
                                      String container, int slot);

    /**
     * Registers a container your plugin owns so the items inside it take part in duplicate
     * detection. Without this, an item hidden in your storage can never be reported as a duplicate.
     *
     * @param source the container
     * @since 1.0.0
     */
    void registerContainerSource(ContainerSource source);

    /**
     * Stops including a container in duplicate detection.
     *
     * @param source the container passed to {@link #registerContainerSource}
     * @since 1.0.0
     */
    void unregisterContainerSource(ContainerSource source);

    /**
     * The identifiers of every module Warden knows about.
     *
     * @return the module ids, in the order Warden lists them
     * @since 1.0.0
     */
    Set<String> getModuleIds();

    /**
     * Whether a module is switched on and running.
     *
     * @param moduleId the module
     * @return whether it is running now
     * @since 1.0.0
     */
    boolean isModuleEnabled(String moduleId);

    /**
     * How far this API has grown since it was first published.
     *
     * <p>Check it before calling anything added after the version you built against. A plugin
     * compiled against a newer X-Warden and run on an older one hits {@code NoSuchMethodError} at
     * the call site, which is a stack trace nobody can act on; refusing politely against a number
     * is better for everybody. Nothing is ever removed or narrowed, so a plugin built against a
     * lower number always works.
     *
     * <table border="1">
     *   <caption>What each version added</caption>
     *   <tr><th>Version</th><th>X-Warden</th><th>Added</th></tr>
     *   <tr><td>1</td><td>1.0.0</td><td>The area APIs, this method, the addon contract and the
     *       addon registry methods</td></tr>
     *   <tr><td>2</td><td>1.0.0</td><td>{@link #unloadAddon(String)}</td></tr>
     *   <tr><td>3</td><td>1.1.0</td><td>{@link #addonFileNames()}</td></tr>
     *   <tr><td>4</td><td>1.1.0</td><td>{@link #getClientApi()}, the punishment, quarantine and
     *       live-sighting methods on the staff and integrity APIs,
     *       {@link dev.drawethree.xwarden.api.flags.WardenAction#PUNISH}, and the four events
     *       added in 1.1.0</td></tr>
     * </table>
     *
     * @return the version number of the surface the running X-Warden implements
     * @since 1.0.0
     */
    int apiVersion();

    /**
     * The findings X-Warden has recorded, and marking them as dealt with.
     *
     * @return the flags API
     * @since 1.0.0
     */
    XWardenFlagsAPI getFlagsApi();

    /**
     * Where the money went.
     *
     * @return the ledger API
     * @since 1.0.0
     */
    XWardenLedgerAPI getLedgerApi();

    /**
     * The economy being watched, and reversing what it should not have paid out.
     *
     * @return the economy API
     * @since 1.0.0
     */
    XWardenEconomyAPI getEconomyApi();

    /**
     * Duplicated items, proven rather than suspected.
     *
     * @return the integrity API
     * @since 1.0.0
     */
    XWardenIntegrityAPI getIntegrityApi();

    /**
     * Accounts that look like the same hands. Read-only; there is no action path here.
     *
     * @return the network API
     * @since 1.0.0
     */
    XWardenNetworkAPI getNetworkApi();

    /**
     * How much like a machine somebody is mining.
     *
     * @return the automation API
     * @since 1.0.0
     */
    XWardenAutomationAPI getAutomationApi();

    /**
     * The five modules, the checks inside them, and whether those checks can actually fire.
     *
     * @return the modules API
     * @since 1.0.0
     */
    XWardenModulesAPI getModulesApi();

    /**
     * The client each player joined with, and what X-Warden made of it.
     *
     * @return the client API
     * @since 1.1.0
     */
    XWardenClientAPI getClientApi();

    /**
     * What a staff member can do, and what the server currently looks like.
     *
     * @return the staff API
     * @since 1.0.0
     */
    XWardenStaffAPI getStaffApi();

    /**
     * Reading and changing X-Warden's configuration.
     *
     * @return the config API
     * @since 1.0.0
     */
    XWardenConfigAPI getConfigApi();

    /**
     * Every addon X-Warden has loaded, enabled or not.
     *
     * @return the loaded addons, in load order
     * @since 1.0.0
     */
    List<XWardenAddonInfo> getLoadedAddons();

    /**
     * Starts a loaded addon that is not running.
     *
     * @param name the addon's name, as its manifest declares it
     * @return {@code false} if there is no addon by that name, or it is already running
     * @since 1.0.0
     */
    boolean enableAddon(String name);

    /**
     * Stops a running addon without letting go of its jar.
     *
     * <p>Only {@code onDisable} runs; the addon stays loaded against the same classloader, so
     * enabling it again runs the code it started with even if the jar on disk has changed. To pick
     * up a replaced jar, {@link #unloadAddon(String)} it and load it again.
     *
     * @param name the addon's name, as its manifest declares it
     * @return {@code false} if there is no addon by that name, or it is not running
     * @since 1.0.0
     */
    boolean disableAddon(String name);

    /**
     * Loads an addon jar that is already in the addons folder, without a restart.
     *
     * <p>Refused if an addon by that name is already loaded. Call {@link #unloadAddon(String)}
     * first to replace a running one.
     *
     * @param filename the jar's file name; it must sit directly in {@code plugins/X-Warden/addons/}
     * @return whether the addon was loaded and enabled
     * @since 1.0.0
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
     * {@link #loadAddonFromFile(String)} brings it back. Refused while another loaded addon
     * declares it as a dependency.
     *
     * @param name the addon's name, as its manifest declares it
     * @return {@code false} if no addon by that name is loaded, or another one depends on it
     * @since 1.0.0
     */
    boolean unloadAddon(String name);

    /**
     * The jars sitting in {@code plugins/X-Warden/addons/}, loaded or not.
     *
     * <p>What {@link #loadAddonFromFile(String)} will accept. A jar's file name is rarely the
     * addon's name - {@code X-Warden-Dashboard-1.0.0.jar} holds an addon called {@code Dashboard} -
     * so anything offering a choice of jars has to read the folder rather than the loaded list.
     *
     * @return the file names, in directory order
     * @since 1.1.0
     */
    List<String> addonFileNames();

    /**
     * Registers where a web panel addon can be reached, so {@code /xwarden} can tell staff.
     *
     * @param url the address staff should open, or {@code null} on disable
     * @since 1.0.0
     */
    void setDashboardUrl(String url);

    /**
     * The URL a panel addon registered.
     *
     * @return the address, or {@code null} if no panel is running
     * @since 1.0.0
     */
    String getDashboardUrl();

    /**
     * Holds the running instance. Set by the X-Warden plugin on enable and cleared on disable.
     *
     * @since 1.0.0
     */
    final class InstanceHolder {

        private static volatile XWardenAPI instance;

        private InstanceHolder() {
            throw new UnsupportedOperationException("Cannot instantiate");
        }

        /**
         * Publishes the running instance. X-Warden calls this itself.
         *
         * <p>Clearing it is always allowed, because that is how X-Warden shuts down. Replacing a
         * live instance is refused: this field is what every integrator resolves X-Warden through,
         * and a plugin that overwrote it - by accident or otherwise - would silently redirect all of
         * them.
         *
         * @param api the instance, or {@code null} to clear it
         * @throws IllegalStateException if an instance is already published
         * @since 1.0.0
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
