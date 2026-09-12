/**
 * The Bukkit events X-Warden fires.
 *
 * <p>Two can be cancelled - {@link dev.drawethree.xwarden.api.event.WardenViolationEvent} before a
 * finding is recorded, and {@link dev.drawethree.xwarden.api.event.WardenPunishmentApplyEvent}
 * before a punishment is applied. The rest announce something that has already happened. Read each
 * event's documentation for which thread it fires on: the violation and flag-resolved events are
 * usually asynchronous, the rest fire on the server thread.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.event;
