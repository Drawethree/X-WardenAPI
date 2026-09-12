/**
 * The economy being watched, the ledger of what it did, and rolling back what it should not have.
 *
 * <p>Ask {@link dev.drawethree.xwarden.api.economy.XWardenEconomyAPI#capabilities} before assuming
 * a reading exists: what can be observed depends on which economy is installed. Ledger reads hit
 * the database and must be called off the server thread; applying a rollback moves money and must
 * be called on it.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.economy;
