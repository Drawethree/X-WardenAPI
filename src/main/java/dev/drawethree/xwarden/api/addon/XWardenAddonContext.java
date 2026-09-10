package dev.drawethree.xwarden.api.addon;

import dev.drawethree.xwarden.api.XWardenAPI;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Everything an addon is given when it is enabled, so it never has to reach for a static.
 */
public interface XWardenAddonContext {

    /** X-Warden's API. Already published by the time an addon's {@code onEnable} runs. */
    XWardenAPI getAPI();

    /** A folder of this addon's own, created for it if it does not exist. */
    File getDataFolder();

    /** A logger that says which addon a line came from. */
    Logger getLogger();

    String getAddonName();

    String getAddonVersion();

    String getAddonAuthor();

    /**
     * Registers a Bukkit listener scoped to this addon.
     *
     * <p>Use this rather than {@code Bukkit.getPluginManager().registerEvents}: listeners
     * registered here are unregistered when the addon is disabled, and one that outlives its own
     * classloader is a leak nothing will attribute to you.
     */
    void registerEvents(Listener listener);

    /** The X-Warden plugin instance, for the few APIs that insist on one. */
    Plugin getPlugin();

    /**
     * Runs something on the server thread and waits for it.
     *
     * <p>This is the bridge an addon needs whenever it is doing work on a thread of its own - a web
     * request, a socket, a scheduled job - and has to touch the server to answer it. Runs inline if
     * you are already on the server thread.
     *
     * @throws WardenSyncException if the task threw, or the server did not run it in reasonable
     *                             time. It never blocks for ever: a shutting-down server would
     *                             otherwise park the calling thread permanently
     */
    <T> T call(Callable<T> task);

    /** As {@link #call}, for work with no result. */
    void sync(Runnable task);

    /**
     * Runs something off the server thread.
     *
     * <p>Tasks scheduled here are cancelled when the addon is disabled.
     */
    void async(Runnable task);
}
