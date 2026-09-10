package dev.drawethree.xwarden.api.integrity;

import dev.drawethree.xwarden.api.ContainerSource;
import dev.drawethree.xwarden.api.HeldItem;
import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Duplicated items, proven rather than suspected.
 *
 * <p>X-Warden stamps an identity into the items worth protecting and watches for the same identity
 * existing in two places at once. That is proof, not statistics, which is why it is the one check
 * allowed to act on a first sighting.
 *
 * <p>Only unstackable items can be fingerprinted. An identity lives in the item's metadata, so
 * splitting a stamped stack would hand both halves the same one - a duplicate nobody created,
 * reported at high confidence against an innocent player.
 */
public interface XWardenIntegrityAPI {

    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<FingerprintRecord> fingerprint(String uid);

    /**
     * Every tracked identity with a given status.
     *
     * @param status {@link FingerprintRecord#STATUS_OK} or {@link FingerprintRecord#STATUS_DUPLICATE}
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<FingerprintRecord> withStatus(String status, int limit);

    /** Everywhere one identity was seen during one sweep. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<Sighting> sightings(String uid, long scanId);

    /** How many of one class of item have existed on the server over time. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<CirculationPoint> circulation(String itemClass, int limit);

    /** What the last sweep found, or empty if none has run since the server started. */
    @ThreadSafety(Requirement.ANY)
    Optional<ScanSummary> lastScan();

    /**
     * Runs a sweep of every inventory on the server now.
     *
     * <p>The future completes off the server thread when the sweep finishes.
     */
    @ThreadSafety(Requirement.ANY)
    CompletableFuture<ScanSummary> scanNow();

    /** The identity on an item, if it has one. */
    @ThreadSafety(Requirement.PRIMARY)
    Optional<String> readIdentity(ItemStack item);

    /**
     * Stamps an item so duplicates of it can be proven. Prison pickaxes already carry an identity
     * of their own and are returned unchanged.
     *
     * @return the identifier now on the item, or empty if it cannot carry one
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
     * @return the record, or empty when the item carries no identity and so is not tracked
     */
    @ThreadSafety(Requirement.PRIMARY)
    Optional<HeldItem> describeStored(ItemStack item, java.util.UUID holder, String holderName,
                                      String container, int slot);

    /**
     * Registers a container your plugin owns so the items inside it take part in duplicate
     * detection. Without this, an item hidden in your storage can never be reported as a duplicate.
     */
    @ThreadSafety(Requirement.ANY)
    void registerContainerSource(ContainerSource source);

    @ThreadSafety(Requirement.ANY)
    void unregisterContainerSource(ContainerSource source);
}
