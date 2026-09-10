package dev.drawethree.xwarden.api.module;

import dev.drawethree.xwarden.api.flags.WardenAction;

import java.util.List;

public record CheckSettings(String id,
                            boolean enabled,
                            double sensitivity,
                            int minSamples,
                            WardenAction action,
                            double vlThreshold,
                            double vlDecay,
                            List<String> commands,
                            String sound) {

    /** Blank means this check uses the sound in the alerts block, which is the default for all. */
    public boolean hasOwnSound() {
        return this.sound != null && !this.sound.isBlank();
    }

    public static CheckSettings disabled(String id) {
        return new CheckSettings(id, false, 1.0D, 1, WardenAction.LOG, 1.0D, 1.0D, List.of(), "");
    }

    public static CheckSettings defaults(String id) {
        return new CheckSettings(id, true, 1.0D, 30, WardenAction.ALERT, 5.0D, 1.0D, List.of(), "");
    }
}
