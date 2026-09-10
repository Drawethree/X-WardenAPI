package dev.drawethree.xwarden.api.module;

import java.util.List;

/**
 * A reading of one of X-Warden's four modules and the checks inside it.
 *
 * <p>The module itself is a live object with {@code enable()} and {@code disable()} on it and does
 * not leave the plugin: an addon that could start a module the owner switched off would be able to
 * undo their decision without saying so.
 *
 * @param id              {@code economy}, {@code integrity}, {@code automation} or {@code network}
 * @param enabledInConfig what {@code warden.yml} says
 * @param running         whether it actually started. These differ when a module is switched on but
 *                        the server cannot feed it - the economy module refuses to run without an
 *                        economy rather than reporting on guesswork
 * @param checks          every check the module knows about, with the settings in force
 */
public record ModuleInfo(String id,
                         boolean enabledInConfig,
                         boolean running,
                         List<CheckSettings> checks) {

    public long enabledCheckCount() {
        return this.checks.stream().filter(CheckSettings::enabled).count();
    }
}
