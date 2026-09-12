/**
 * X-Warden's five modules, the checks inside them, the settings in force, and whether each
 * check can actually reach its action.
 *
 * <p>Everything here reads memory and is safe from any thread, except switching a module on or
 * off, which writes the configuration and reloads on the server thread.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.module;
