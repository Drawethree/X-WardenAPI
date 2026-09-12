package dev.drawethree.xwarden.api.integrity;

import dev.drawethree.xwarden.api.ContainerSource;
import dev.drawethree.xwarden.api.HeldItem;
import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Duplicated items, proven rather than suspected.
 *
 * <p>X-Warden stamps an identity into the items worth protecting and watches for the same identity
 * existing in two places at once. That is proof, not statistics, which is why it is one of the two
 * checks allowed to act on a first sighting.
 *
 * <p>Only unstackable items can be fingerprinted. An identity lives in the item's metadata, so
 * splitting a stamped stack would hand both halves the same one - a duplicate nobody created,
 * reported at high confidence against an innocent player.
 *
 * <p>Every unstackable item is stamped the first time X-Warden sees it in a real container, and
 * duplicates are found the moment they appear, not only on a sweep. The copy that should not exist
 * is taken out of play and kept, so a staff member can hand it back. A second holder of one
 * identity looks exactly like a dupe handed to a friend and is treated as one; a third holder is
 * what tells a kit, shop or crate template apart, at which point the earlier findings are withdrawn
 * and the copies handed back. {@link #stripIdentity} is how the template is fixed for good.
 *
 * @since 1.0.0
 */
public interface XWardenIntegrityAPI {

    /**
     * One tracked identity.
     *
     * @param uid the identity
     * @return its record, or empty when X-Warden has never seen it
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<FingerprintRecord> fingerprint(String uid);

    /**
     * Every tracked identity with a given status.
     *
     * @param status {@link FingerprintRecord#STATUS_OK}, {@link FingerprintRecord#STATUS_DUPLICATE}
     *               or {@link FingerprintRecord#STATUS_TEMPLATE}
     * @param limit  the most to return
     * @return the records, most recently seen first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<FingerprintRecord> withStatus(String status, int limit);

    /**
     * Everywhere one identity was seen during one sweep.
     *
     * @param uid    the identity
     * @param scanId which sweep, from {@link ScanSummary#scanId()}
     * @return the sightings
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Sighting> sightings(String uid, long scanId);

    /**
     * How many of one class of item have existed on the server over time.
     *
     * @param itemClass the class
     * @param limit     the most points to return
     * @return the points, newest first
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<CirculationPoint> circulation(String itemClass, int limit);

    /**
     * What the last sweep found.
     *
     * @return the summary, or empty if none has run since the server started
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<ScanSummary> lastScan();

    /**
     * Runs a sweep of every inventory on the server now.
     *
     * @return a future that completes off the server thread when the sweep finishes, or fails
     *         when the integrity module is not running
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    CompletableFuture<ScanSummary> scanNow();

    /**
     * The identity on an item, if it has one.
     *
     * @param item the item
     * @return its identity, or empty
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    Optional<String> readIdentity(ItemStack item);

    /**
     * Stamps an item so duplicates of it can be proven. Prison pickaxes already carry an identity
     * of their own and are returned unchanged.
     *
     * @param item      the item, modified in place
     * @param itemClass the class to record it under
     * @return the identifier now on the item, or empty if it cannot carry one
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    Optional<String> stamp(ItemStack item, String itemClass);

    /**
     * Describes one item your container holds, ready to be handed back from
     * {@link ContainerSource#snapshot()}.
     *
     * <p>Do not work this out yourself. Which scheme an item uses - a prison pickaxe's own, or
     * X-Warden's - depends on what is installed, and only X-Warden knows. Reading an identity
     * touches item metadata, so call this on the server thread while the items are in hand, keep
     * what it gives you, and let the asynchronous {@code snapshot()} return the kept copies.
     *
     * @param item       the item
     * @param holder     whose storage it is in, or {@code null} for a container with no owner
     * @param holderName that holder's name, as it should read to staff
     * @param container  your container's id, as passed to {@link ContainerSource#id()}
     * @param slot       the slot it sits in, or {@code -1} where the container has no slots
     * @return the record, or empty when the item carries no identity and so is not tracked
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    Optional<HeldItem> describeStored(ItemStack item, UUID holder, String holderName,
                                      String container, int slot);

    /**
     * Registers a container your plugin owns so the items inside it take part in duplicate
     * detection. Without this, an item hidden in your storage can never be reported as a duplicate.
     *
     * @param source the container
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void registerContainerSource(ContainerSource source);

    /**
     * Stops including a container in duplicate detection.
     *
     * @param source the container passed to {@link #registerContainerSource}
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    void unregisterContainerSource(ContainerSource source);

    /**
     * Items taken out of play from one player's inventory, newest first.
     *
     * @param player the player, or {@code null} for copies that sat in a block, on the ground or
     *               in an item frame
     * @param limit  the most to return
     * @return the items, newest first
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<QuarantinedItem> quarantined(UUID player, int limit);

    /**
     * The most recent items taken out of play, whoever held them.
     *
     * @param limit the most to return
     * @return the items, newest first
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<QuarantinedItem> recentQuarantine(int limit);

    /**
     * One quarantined item by its id.
     *
     * @param id the row id
     * @return the item, or empty
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<QuarantinedItem> quarantinedItem(long id);

    /**
     * Hands a quarantined item back to the player it was taken from.
     *
     * <p>The item comes back with a fresh identity: staff have decided both copies are legitimate,
     * so they must not share one.
     *
     * @param id    the row id
     * @param actor who did it, for the record
     * @return what happened; {@link RestoreOutcome#NOT_FOUND} also answers when the integrity
     *         module is not running
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    RestoreOutcome restoreQuarantined(long id, String actor);

    /**
     * Where the live index last saw an identity.
     *
     * @param uid the identity
     * @return the sighting, or empty when the index has not seen it since the server started
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<LiveSighting> liveSighting(String uid);

    /**
     * Everything the live index places in one player's hands.
     *
     * @param holder the player
     * @return the sightings; empty when the index has nothing on them
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.ANY)
    List<LiveSighting> liveSightingsOf(UUID holder);

    /**
     * Removes X-Warden's identity from an item, for a kit, shop or crate template that was saved
     * with one on it. A prison pickaxe's own identity is never touched.
     *
     * @param item the item, modified in place
     * @return whether anything was removed
     * @since 1.1.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean stripIdentity(ItemStack item);
}
