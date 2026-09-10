package dev.drawethree.xwarden.api.flags;

import java.util.UUID;

public record ViolationQuery(UUID player, String moduleId, boolean openOnly, int limit) {

    public static ViolationQuery all(int limit) {
        return new ViolationQuery(null, null, false, limit);
    }

    public static ViolationQuery forPlayer(UUID player, int limit) {
        return new ViolationQuery(player, null, false, limit);
    }

    public static ViolationQuery forModule(String moduleId, int limit) {
        return new ViolationQuery(null, moduleId, false, limit);
    }

    public ViolationQuery inModule(String moduleId) {
        return new ViolationQuery(this.player, moduleId, this.openOnly, this.limit);
    }

    public ViolationQuery openOnly(boolean only) {
        return new ViolationQuery(this.player, this.moduleId, only, this.limit);
    }
}
