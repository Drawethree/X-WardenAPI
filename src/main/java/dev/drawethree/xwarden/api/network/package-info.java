/**
 * Accounts that look like the same hands, and the sessions the correlation is drawn from.
 *
 * <p>Read-only by design: there is no action path for a link anywhere in X-Warden. Stored links
 * and sessions are read off the server thread; the last computed set is safe from anywhere.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.network;
