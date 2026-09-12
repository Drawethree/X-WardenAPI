/**
 * How much like a machine somebody is mining: the score, the signals behind it, and calibration.
 *
 * <p>A score is a reading, never a verdict. Everything here is safe to read from any thread; only
 * scoring on demand, issuing a challenge and starting or applying calibration touch the server and
 * must be called from its thread.
 *
 * @since 1.0.0
 */
package dev.drawethree.xwarden.api.automation;
