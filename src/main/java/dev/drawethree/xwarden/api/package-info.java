/**
 * The public surface of X-Warden.
 *
 * <p>{@link dev.drawethree.xwarden.api.XWardenAPI} is the way in: resolve it with
 * {@code XWardenAPI.getInstance()} or through Bukkit's {@code ServicesManager}, then reach the
 * area APIs from it. Every method on an area API declares which thread it may be called from with
 * {@link dev.drawethree.xwarden.api.ThreadSafety}, and X-Warden enforces the declaration.
 *
 * <p>The records here - {@link dev.drawethree.xwarden.api.HeldItem},
 * {@link dev.drawethree.xwarden.api.MultiplierBreakdown},
 * {@link dev.drawethree.xwarden.api.AutomationBreakdown} - are readings taken at one moment, never
 * live objects; nothing that can change X-Warden's state leaves the plugin.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api;
