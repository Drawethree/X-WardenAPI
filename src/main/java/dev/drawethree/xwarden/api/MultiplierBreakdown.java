package dev.drawethree.xwarden.api;

import java.math.BigDecimal;

/**
 * What happened to one payout as it travelled through the listeners that multiply it.
 * <p>
 * The core has no single method that resolves a total multiplier, so these figures are measured
 * from the event itself rather than recomputed. {@code highBandFactor} covers pickaxe skins,
 * pickaxe quality and the multipliers module together, because those listeners share an event
 * priority and cannot be told apart from outside.
 *
 * @param base                the amount before anything multiplied it
 * @param credited            the amount the player actually received
 * @param lowBandFactor       what enchant reward multipliers did to it
 * @param highBandFactor      what skins, quality and the multipliers module did to it together
 * @param effectiveMultiplier what the multipliers module reports for this player, or null
 * @param residual            the part of the high band the multipliers module does not explain
 * @param totalFactor         credited divided by base
 */
public record MultiplierBreakdown(BigDecimal base,
                                  BigDecimal credited,
                                  BigDecimal lowBandFactor,
                                  BigDecimal highBandFactor,
                                  BigDecimal effectiveMultiplier,
                                  BigDecimal residual,
                                  BigDecimal totalFactor) {
}
