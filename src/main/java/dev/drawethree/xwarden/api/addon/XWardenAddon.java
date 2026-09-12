package dev.drawethree.xwarden.api.addon;

/**
 * A modular addon for X-Warden.
 *
 * <p>Drop a jar into {@code plugins/X-Warden/addons/} and X-Warden finds it, loads it in an order
 * that respects what it declares it depends on, hands it a context and enables it. It is unloaded
 * cleanly on shutdown, in reverse order.
 *
 * <p>An addon jar declares itself in its {@code META-INF/MANIFEST.MF}. One attribute is required:
 * <ul>
 *   <li>{@code X-Warden-Addon-Class} - the class implementing this interface. It must have a
 *       public no-argument constructor.</li>
 * </ul>
 * The rest are optional, and default as noted:
 * <ul>
 *   <li>{@code X-Warden-Addon-Name} - the display name, and the name of its data folder
 *       (default: {@code Unknown}, which is worth not accepting)</li>
 *   <li>{@code X-Warden-Addon-Version} (default: {@code Unknown})</li>
 *   <li>{@code X-Warden-Addon-Author} (default: {@code Unknown})</li>
 *   <li>{@code X-Warden-Addon-Description} (default: {@code No description})</li>
 *   <li>{@code X-Warden-Min-Version} - the oldest X-Warden this works with; an older X-Warden
 *       skips the addon and says so</li>
 *   <li>{@code X-Warden-Priority} - load order, default {@code 50}, lower loads first</li>
 *   <li>{@code X-Warden-Depends} - comma-separated addon names guaranteed to load first. An addon
 *       whose dependency is absent is skipped, and told so by name in the console</li>
 * </ul>
 *
 * <p>An addon runs inside X-Warden's process on the X-Warden classloader's child, so anything it
 * starts - a thread, a socket, a server - is its own to stop in {@link #onDisable()}.
 *
 * @since 1.0.0
 */
public interface XWardenAddon {

    /**
     * Called when the addon is being enabled.
     *
     * <p>The API is already published by the time this runs, so {@link XWardenAddonContext#getAPI()}
     * is safe to call immediately. Throwing from here disables this addon and nothing else.
     *
     * @param context everything the addon is given: the API, its folder, its logger, and the
     *                bridges to and from the server thread
     * @since 1.0.0
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
     * <p>Do not block indefinitely. This runs during server shutdown, and X-Warden bounds how long
     * it waits before reporting the addon as hung and carrying on.
     *
     * @since 1.0.0
     */
    void onDisable();
}
