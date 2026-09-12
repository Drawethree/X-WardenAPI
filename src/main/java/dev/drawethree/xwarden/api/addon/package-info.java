/**
 * The addon contract: what a jar in {@code plugins/X-Warden/addons/} implements, and what it is
 * given when it is enabled.
 *
 * <p>An addon runs inside X-Warden's process. It reaches the server thread through
 * {@link dev.drawethree.xwarden.api.addon.XWardenAddonContext#call} and leaves it through
 * {@link dev.drawethree.xwarden.api.addon.XWardenAddonContext#async}; listeners and tasks that go
 * through the context are released for it on disable, anything else it started is its own to stop.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.addon;
