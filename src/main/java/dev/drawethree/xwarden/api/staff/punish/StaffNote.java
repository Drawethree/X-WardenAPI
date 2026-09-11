package dev.drawethree.xwarden.api.staff.punish;

import java.util.UUID;

/** A line a staff member wrote about a player, kept with the punishment history. */
public record StaffNote(long id,
                        UUID player,
                        String playerName,
                        String note,
                        UUID staffId,
                        String staffName,
                        long at) {
}
