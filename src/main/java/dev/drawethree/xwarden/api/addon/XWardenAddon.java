package dev.drawethree.xwarden.api.addon;

/**
 * A modular addon for X-Warden.
 *
 * <p>Drop a jar into {@code plugins/X-Warden/addons/} and X-Warden finds it, loads it in an order
 * that respects what it declares it depends on, hands it a context and enables it. It is unloaded
 * cleanly on shutdown, in reverse order.
 *
 * <p>Every addon jar must declare these in its manifest:
 * <ul>
 *   <li>{@code X-Warden-Addon-Class} - the class implementing this interface (required)</li>
 *   <li>{@code X-Warden-Addon-Name} - the display name, and the name of its data folder</li>
 *   <li>{@code X-Warden-Addon-Version}</li>
 *   <li>{@code X-Warden-Addon-Author}</li>
 *   <li>{@code X-Warden-Addon-Description}</li>
 *   <li>{@code X-Warden-Min-Version} - the oldest X-Warden this works with (optional)</li>
 *   <li>{@code X-Warden-Priority} - load order, default {@code 50}, lower loads first (optional)</li>
 *   <li>{@code X-Warden-Depends} - comma-separated addon names guaranteed to load first. An addon
 *       whose dependency is absent is skipped, and told so by name in the console (optional)</li>
 * </ul>
 *
 * <p>The class must have a no-argument constructor.
 */
public interface XWardenAddon {

    /**
     * Called when the addon is being enabled.
     *
     * <p>The API is already published by the time this runs, so {@link XWardenAddonContext#getAPI()}
     * is safe to call immediately. Throwing from here disables this addon and nothing else.
     */
    void onEnable(XWardenAddonContext context);

    /**
     * Called when the addon is being disabled.
     *
     * <p>Listeners registered through the context are released for you, and so are tasks scheduled
     * through it. Anything else you started - a thread, a socket, a server - is yours to stop, and
     * you must stop it here: X-Warden closes your classloader once this returns, and a thread still
     * running against it keeps the whole thing alive and fails in ways nothing will explain.
     *
     * <p>Do not block indefinitely. This runs during server shutdown.
     */
    void onDisable();
}
