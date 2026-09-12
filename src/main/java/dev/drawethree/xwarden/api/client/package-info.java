/**
 * The client each player joined with, and what X-Warden concluded from its brand, its channels and
 * the keybind probe.
 *
 * <p>Readings are held in memory and safe from any thread; asking for a re-check touches the
 * server and must be called from its thread.
 *
 * @since 1.1.0
 */
package dev.drawethree.xwarden.api.client;
