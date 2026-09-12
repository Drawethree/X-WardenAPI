package dev.drawethree.xwarden.api.config;

import java.util.List;

/**
 * One setting, the value in force, and where that value came from.
 *
 * <p>X-Warden resolves a setting through three layers: the owner's file, the preset it names, and
 * the defaults bundled in the jar. Which layer answered is not a detail - an editor that hides it
 * leaves an owner changing a value in a file that is not the one being read.
 *
 * @param path              the dotted path, as {@code modules.economy.checks.income-per-block.enabled}
 * @param value             the value in force, rendered as text
 * @param type              {@code boolean}, {@code number}, {@code string} or {@code list}
 * @param comment           the comment lines above it in the bundled file, which is where the
 *                          reasoning for every default is written down
 * @param source            which layer supplied the value
 * @param editable          whether a write can reach it. A list, a block scalar or a key the
 *                          owner's file does not contain at all cannot be edited in place
 * @param shadowedByPreset  whether writing the bundled default here would be ignored. An owner
 *                          value identical to the bundled default defers to the preset, so
 *                          "reset to default" on such a key quietly yields the preset's value
 *                          instead of the default's
 * @since 1.0.0
 */
public record ConfigNode(String path,
                         String value,
                         String type,
                         List<String> comment,
                         Source source,
                         boolean editable,
                         boolean shadowedByPreset) {

    /**
     * Which of the three layers answered for this setting.
     *
     * @since 1.0.0
     */
    public enum Source {
        /** The server owner's own file. */
        OWN,
        /** The preset named in that file. */
        PRESET,
        /** The defaults bundled in the jar. */
        BUNDLED
    }
}
