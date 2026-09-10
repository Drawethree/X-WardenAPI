package dev.drawethree.xwarden.api.economy;

/**
 * What an economy can be observed to do on the server X-Warden happens to be running on.
 * <p>
 * X-Prison publishes an event for every credit and debit before it is written; Vault publishes
 * nothing at all and can only be watched for balance changes. A check whose capability is absent is
 * switched off with a line in the console saying why, never left running on guesswork - so an
 * integrator should ask before assuming a reading is available.
 */
public enum EconomyCapability {

    /** Individual transactions can be recorded. */
    LEDGER,
    /** Who paid whom is known, which is what cycle detection needs. */
    COUNTERPARTIES,
    /** Amounts can be inspected and refused before they reach a balance. */
    INVARIANT_GUARD,
    /** A payout can be measured at each stage that multiplies it. */
    MULTIPLIER_AUDIT,
    /** Balances can be adjusted, which is what rollback needs. */
    ADJUSTMENT
}
