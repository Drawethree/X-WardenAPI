package dev.drawethree.xwarden.api.module;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * X-Warden's five modules, the checks inside them, and whether those checks can actually fire.
 */
public interface XWardenModulesAPI {

    @ThreadSafety(Requirement.ANY)
    List<String> moduleIds();

    @ThreadSafety(Requirement.ANY)
    List<ModuleInfo> modules();

    @ThreadSafety(Requirement.ANY)
    Optional<ModuleInfo> module(String moduleId);

    @ThreadSafety(Requirement.ANY)
    boolean isEnabled(String moduleId);

    /** Switches a module on or off, writes it to the configuration, and reloads. */
    @ThreadSafety(Requirement.PRIMARY)
    boolean setEnabled(String moduleId, boolean enabled);

    @ThreadSafety(Requirement.ANY)
    Map<String, CheckSettings> checks(String moduleId);

    @ThreadSafety(Requirement.ANY)
    Optional<CheckSettings> check(String moduleId, String checkId);

    /**
     * Whether each check can reach its action on the settings actually in force.
     *
     * <p>Worth showing an owner unprompted. A check whose ceiling sits under its threshold detects
     * exactly what it was built for and tells nobody, silently, for ever - and nothing else on the
     * server will ever mention it.
     */
    @ThreadSafety(Requirement.ANY)
    List<CheckReachability> reachability();

    /**
     * Registers a check identifier your plugin reports into, so its findings get real settings
     * instead of being discarded.
     *
     * <p>Call once on enable, before your first report. The check picks up whatever the owner has
     * written under {@code modules.<module>.checks.<check>}, and X-Warden's ordinary defaults where
     * they have written nothing - so a plugin cannot ship a check that punishes on its own. Only
     * the owner can raise it.
     *
     * @return false if the module id is not one X-Warden knows, in which case nothing reported
     *         under it will ever be raised
     */
    @ThreadSafety(Requirement.ANY)
    boolean registerCheck(String moduleId, String checkId);
}
