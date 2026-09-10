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
 */
public interface XWardenConfigAPI {

    /** Which of X-Warden's three files to read or write. */
    enum Target {
        /** {@code warden.yml} - everything the plugin does. */
        WARDEN,
        /** {@code warden-gui.yml} - the in-game menus. */
        GUI,
        /** {@code warden-messages.yml} - every string a player or staff member sees. */
        MESSAGES
    }

    /** Every setting in a file, with the value in force and where it came from. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<ConfigNode> read(Target target);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    Optional<ConfigNode> read(Target target, String path);

    /** The file as it is on disk, comments and all. */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    String raw(Target target);

    @ThreadSafety(Requirement.OFF_PRIMARY)
    WriteResult set(Target target, String path, String value);

    /**
     * Several writes as one. Either every one succeeds or the file is left exactly as it was - a
     * form that saved half its fields is worse than one that saved none.
     */
    @ThreadSafety(Requirement.OFF_PRIMARY)
    List<WriteResult> setAll(Target target, Map<String, String> values);

    @ThreadSafety(Requirement.ANY)
    String preset();

    @ThreadSafety(Requirement.ANY)
    List<String> presets();

    /** Switches preset and reloads. */
    @ThreadSafety(Requirement.PRIMARY)
    boolean setPreset(String name);

    /**
     * Re-reads all three files and reloads every module.
     *
     * <p>Storage settings are read once at startup and cannot change here; a changed one is
     * reported rather than silently ignored.
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean reload();
}
