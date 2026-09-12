/**
 * The findings X-Warden records: what they carry, how to query them, and marking them handled.
 *
 * <p>A finding carries a confidence and the evidence behind it, never a recommendation. Reads and
 * resolutions hit the database and must be called off the server thread; the in-memory recent list
 * and the guidance text are safe from anywhere.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.flags;
