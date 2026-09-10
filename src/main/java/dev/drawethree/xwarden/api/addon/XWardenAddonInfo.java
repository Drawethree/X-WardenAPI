package dev.drawethree.xwarden.api.addon;

import java.util.List;

/**
 * What X-Warden knows about a loaded addon, read from its manifest.
 *
 * @param minVersion the oldest X-Warden it claims to work with, or null
 * @param priority   load order; lower loads first
 * @param depends    addon names guaranteed to have loaded before it
 * @param enabled    whether it is running now, as opposed to merely loaded
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
