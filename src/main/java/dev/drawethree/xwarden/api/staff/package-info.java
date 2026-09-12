/**
 * What a staff member can do - freeze, watch, punish, annotate, export - and readings of the server
 * and its players.
 *
 * <p>Actions that touch a player run on the server thread; readings that hit the database run off
 * it; levels and freeze state are safe from anywhere.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.staff;
