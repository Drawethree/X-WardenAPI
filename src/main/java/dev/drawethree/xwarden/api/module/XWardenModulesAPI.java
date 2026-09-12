package dev.drawethree.xwarden.api.module;

import dev.drawethree.xwarden.api.ThreadSafety;
import dev.drawethree.xwarden.api.ThreadSafety.Requirement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * X-Warden's five modules - {@code economy}, {@code integrity}, {@code automation},
 * {@code network} and {@code client} - the checks inside them, and whether those checks can
 * actually fire.
 *
 * @since 1.0.0
 */
public interface XWardenModulesAPI {

    /**
     * The five module ids, in the order X-Warden lists them.
     *
     * @return the ids
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<String> moduleIds();

    /**
     * A reading of every module.
     *
     * @return one per module, in order
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    List<ModuleInfo> modules();

    /**
     * A reading of one module.
     *
     * @param moduleId the module
     * @return the reading, or empty for an id X-Warden does not know
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<ModuleInfo> module(String moduleId);

    /**
     * Whether a module is switched on and running.
     *
     * @param moduleId the module
     * @return whether it is running now
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean isEnabled(String moduleId);

    /**
     * Switches a module on or off, writes it to {@code warden.yml}, and reloads.
     *
     * @param moduleId the module
     * @param enabled  whether it should run
     * @return {@code false} for an id X-Warden does not know, or when the write or the reload
     *         failed
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.PRIMARY)
    boolean setEnabled(String moduleId, boolean enabled);

    /**
     * Every check in a module, with the settings in force.
     *
     * @param moduleId the module
     * @return check id to settings, in file order; empty for an unknown module
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Map<String, CheckSettings> checks(String moduleId);

    /**
     * One check's settings in force.
     *
     * @param moduleId the module
     * @param checkId  the check
     * @return the settings, or empty when the module does not have that check
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    Optional<CheckSettings> check(String moduleId, String checkId);

    /**
     * Whether each check can reach its action on the settings actually in force.
     *
     * <p>Worth showing an owner unprompted. A check whose ceiling sits under its threshold detects
     * exactly what it was built for and tells nobody, silently, for ever - and nothing else on the
     * server will ever mention it.
     *
     * @return one entry per check
     * @since 1.0.0
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
     * @param moduleId the module
     * @param checkId  your check id
     * @return {@code false} if the module id is not one X-Warden knows, in which case nothing
     *         reported under it will ever be raised
     * @since 1.0.0
     */
    @ThreadSafety(Requirement.ANY)
    boolean registerCheck(String moduleId, String checkId);
}
