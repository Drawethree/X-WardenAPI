package dev.drawethree.xwarden.api.addon;

import java.util.List;

/**
 * What X-Warden knows about a loaded addon, read from its manifest.
 *
 * @param name        the display name, and the name of its data folder
 * @param version     what it declares as its version
 * @param author      who wrote it
 * @param description one line on what it does
 * @param minVersion  the oldest X-Warden it claims to work with, or {@code null}
 * @param priority    load order; lower loads first
 * @param depends     addon names guaranteed to have loaded before it
 * @param enabled     whether it is running now, as opposed to merely loaded
 * @since 1.0.0
 */
public record XWardenAddonInfo(String name,
                               String version,
                               String author,
                               String description,
                               String minVersion,
                               int priority,
                               List<String> depends,
                               boolean enabled) {
}
