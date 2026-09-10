package dev.drawethree.xwarden.api;

import java.util.List;

/**
 * Lets an addon that stores items on a player's behalf take part in duplicate detection.
 * <p>
 * Without a source registered for it, a container is invisible to the scanner and an item hidden
 * there will never be reported as a duplicate.
 */
public interface ContainerSource {

    /**
     * A stable identifier for this container, shown to staff in the evidence view.
     */
    String id();

    /**
     * Every fingerprinted item this container currently holds.
     * <p>
     * Called from an asynchronous task, so the returned list must already be materialised and the
     * implementation must not touch Bukkit state while producing it.
     */
    List<HeldItem> snapshot();
}
