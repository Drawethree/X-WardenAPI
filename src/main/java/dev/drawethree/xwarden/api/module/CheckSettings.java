package dev.drawethree.xwarden.api.module;

import dev.drawethree.xwarden.api.flags.WardenAction;

import java.util.List;

/**
 * One check as configured.
 *
 * @param id          the check id, as spelled in {@code warden.yml}
 * @param enabled     whether it runs at all
 * @param sensitivity 1.0 is the preset default; below is stricter, above more forgiving
 * @param minSamples  how much data it needs before it is allowed an opinion
 * @param action      what happens when the level reaches the threshold
 * @param vlThreshold the violation level the action fires at
 * @param vlDecay     how much level drains away per minute
 * @param commands    what {@link WardenAction#COMMAND} runs, as the console
 * @param sound       the alert sound, or blank for the one in the alerts block
 * @param preset the punishment preset applied when the action is {@link WardenAction#PUNISH};
 *               blank for every other action
 * @since 1.0.0
 */
public record CheckSettings(String id,
                            boolean enabled,
                            double sensitivity,
                            int minSamples,
                            WardenAction action,
                            double vlThreshold,
                            double vlDecay,
                            List<String> commands,
                            String sound,
                            String preset) {

    /**
     * The shape before punishment presets existed; kept so anything built against it still links.
     *
     * @param id          the check id
     * @param enabled     whether it runs
     * @param sensitivity the sensitivity
     * @param minSamples  the sample floor
     * @param action      the action
     * @param vlThreshold the threshold
     * @param vlDecay     the decay per minute
     * @param commands    the commands
     * @param sound       the sound, or blank
     * @since 1.0.0
     */
    public CheckSettings(String id, boolean enabled, double sensitivity, int minSamples,
                         WardenAction action, double vlThreshold, double vlDecay,
                         List<String> commands, String sound) {
        this(id, enabled, sensitivity, minSamples, action, vlThreshold, vlDecay, commands, sound, "");
    }

    /**
     * Whether this check names a sound of its own.
     *
     * @return {@code false} when it uses the sound in the alerts block, which is the default
     * @since 1.0.0
     */
    public boolean hasOwnSound() {
        return this.sound != null && !this.sound.isBlank();
    }

    /**
     * Whether a punishment preset is named for {@link WardenAction#PUNISH}.
     *
     * @return whether {@link #preset()} is set
     * @since 1.1.0
     */
    public boolean hasPreset() {
        return this.preset != null && !this.preset.isBlank();
    }

    /**
     * A check that is switched off.
     *
     * @param id the check id
     * @return settings with {@code enabled} false
     * @since 1.0.0
     */
    public static CheckSettings disabled(String id) {
        return new CheckSettings(id, false, 1.0D, 1, WardenAction.LOG, 1.0D, 1.0D, List.of(), "", "");
    }

    /**
     * What a check registered by a plugin gets when the owner has written nothing for it: enabled,
     * ALERT, thirty samples, a threshold of five and a decay of one per minute.
     *
     * @param id the check id
     * @return the defaults
     * @since 1.0.0
     */
    public static CheckSettings defaults(String id) {
        return new CheckSettings(id, true, 1.0D, 30, WardenAction.ALERT, 5.0D, 1.0D, List.of(), "", "");
    }
}
