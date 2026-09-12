package dev.drawethree.xwarden.api.config;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reading and changing X-Warden's configuration.
 *
 * <p>A write edits the one line the setting lives on and leaves every other byte of the file
 * untouched. That is not fastidiousness: roughly six hundred of {@code warden.yml}'s thousand lines
 * are comments explaining what each default is for and what a false positive on each check looks
 * like, and re-emitting the document through a YAML writer degrades all of it a little every time.
 *
 * <p>Anything the writer cannot locate unambiguously it refuses, and says which refusal it was.
 * Check {@link WriteResult#effective()} rather than assuming a successful write took effect: a
 * value can be correctly written and still be overridden by the preset.
 *
 * @since 1.0.0
 */
public interface XWardenConfigAPI {

    /**
     * Which of X-Warden's editable files to read or write.
     *
     * <p>The other files - punishments, client signatures, vulnerabilities, anticheats - are read
     * from disk on reload and are not reachable through this API.
     *
     * @since 1.0.0
     */
    enum Target {
        /** {@code warden.yml} - everything the plugin does. */
        WARDEN,
        /** {@code warden-gui.yml} - the in-game menus. */
        GUI,
        /** {@code warden-messages.yml} - every string a player or staff member sees. */
        MESSAGES
    }

    /**
     * Every setting in a file, with the value in force and where it came from.
     *
     * @param target which file
     * @return one node per key, in file order
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<ConfigNode> read(Target target);

    /**
     * One setting, with the value in force and where it came from.
     *
     * @param target which file
     * @param path   the dotted path
     * @return the node, or empty when no line in the file holds that path
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<ConfigNode> read(Target target, String path);

    /**
     * The file as it is on disk, comments and all.
     *
     * @param target which file
     * @return its text, or an empty string when it cannot be read
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    String raw(Target target);

    /**
     * Rewrites the one line a setting lives on.
     *
     * @param target which file
     * @param path   the dotted path
     * @param value  the new value, as it should appear in the file
     * @return what happened, and what the setting reads as now
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    WriteResult set(Target target, String path, String value);

    /**
     * Several writes as one. Either every one succeeds or the file is left exactly as it was - a
     * form that saved half its fields is worse than one that saved none.
     *
     * @param target which file
     * @param values path to new value
     * @return one result per value attempted, in order; the list stops at the first refusal
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<WriteResult> setAll(Target target, Map<String, String> values);

    /**
     * The preset {@code warden.yml} names.
     *
     * @return its name, such as {@code balanced}
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    String preset();

    /**
     * Every preset installed in {@code plugins/X-Warden/presets/}.
     *
     * @return their names, without the {@code .yml}
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<String> presets();

    /**
     * Switches preset and reloads.
     *
     * @param name one of {@link #presets()}
     * @return {@code false} when there is no such preset, or the write or reload failed
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean setPreset(String name);

    /**
     * Re-reads every configuration file and restarts every module.
     *
     * <p>Storage settings are read once at startup and cannot change here; a changed one is
     * reported in the console rather than silently ignored.
     *
     * @return whether every file could be read; a broken file is left alone and the previous
     *         settings stay in force
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean reload();
}
