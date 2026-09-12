package dev.drawethree.xwarden.api.staff.punish;

import java.util.UUID;

/**
 * A line a staff member wrote about a player, kept with the punishment history.
 *
 * @param id         the row id
 * @param player     who it is about
 * @param playerName their name
 * @param note       the text
 * @param staffId    who wrote it, or {@code null} for the console or a plugin
 * @param staffName  who wrote it, in words
 * @param at         when, as epoch milliseconds
 * @since 1.1.0
 */
public record StaffNote(long id,
                        UUID player,
                        String playerName,
                        String note,
                        UUID staffId,
                        String staffName,
                        long at) {
}
