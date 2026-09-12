/**
 * Reading and changing X-Warden's configuration files without disturbing the comments in them.
 *
 * <p>Reads and writes touch disk and must be called off the server thread; switching preset and
 * reloading restart the modules and must be called on it.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.config;
