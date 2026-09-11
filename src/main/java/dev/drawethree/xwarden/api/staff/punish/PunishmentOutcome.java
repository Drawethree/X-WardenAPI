package dev.drawethree.xwarden.api.staff.punish;

/** Why a punishment was not applied, or that it was. */
public enum PunishmentOutcome {
    APPLIED,
    /** The punishment system is switched off in {@code warden-punishments.yml}. */
    DISABLED,
    /** No preset by that id. */
    UNKNOWN_PRESET,
    /** The step needs the player's address and they are not online. */
    PLAYER_OFFLINE,
    /** A plugin cancelled it through {@code WardenPunishmentApplyEvent}. */
    VETOED,
    /** The record could not be written, so nothing was applied either. */
    FAILED
}
