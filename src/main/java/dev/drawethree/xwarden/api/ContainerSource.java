package dev.drawethree.xwarden.api;

import java.util.List;

/**
 * Lets a plugin that stores items on a player's behalf take part in duplicate detection.
 *
 * <p>Without a source registered for it, a container is invisible to the scanner and an item hidden
 * there will never be reported as a duplicate. Register one with
 * {@link dev.drawethree.xwarden.api.integrity.XWardenIntegrityAPI#registerContainerSource} on
 * enable and unregister it on disable.
 *
 * @since 1.0.0
 */
public interface ContainerSource {

    /**
     * A stable identifier for this container, shown to staff in the evidence view.
     *
     * @return the identifier, such as {@code playervaults}
     * @since 1.0.0
     */
    String id();

    /**
     * Every fingerprinted item this container currently holds.
     *
     * <p>Called from an asynchronous task, so the returned list must already be materialised and the
     * implementation must not touch Bukkit state while producing it. Build the entries on the server
     * thread with {@link XWardenAPI#describeStored} as items move, and hand back the kept copies.
     *
     * @return the items, never {@code null}
     * @since 1.0.0
     */
    List<HeldItem> snapshot();
}
