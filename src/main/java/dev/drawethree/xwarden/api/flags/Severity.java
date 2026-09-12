package dev.drawethree.xwarden.api.flags;

/**
 * The band a finding's confidence falls in.
 *
 * <p>A number on its own tells a moderator nothing. Every finding carries a band so that "78" reads
 * as "strong" rather than as a licence to ban. The colour, symbol and material here are the
 * defaults; what staff actually see comes from {@code warden-messages.yml} and
 * {@code warden-gui.yml}, keyed by {@link #getKey()}.
 *
 * @since 1.0.0
 */
public enum Severity {

    /** 85 and above: the check is about as sure as it gets. */
    CRITICAL(85, "critical", "<dark_red>", "⛔", "REDSTONE_BLOCK"),
    /** 70 to 84. */
    STRONG(70, "strong", "<red>", "⚠", "ORANGE_DYE"),
    /** 50 to 69. */
    WORTH_A_LOOK(50, "worth-a-look", "<gold>", "◆", "YELLOW_DYE"),
    /** Below 50. */
    WEAK(0, "weak", "<gray>", "●", "LIGHT_GRAY_DYE");

    private final int floor;
    private final String key;
    private final String colour;
    private final String symbol;
    private final String material;

    Severity(int floor, String key, String colour, String symbol, String material) {
        this.floor = floor;
        this.key = key;
        this.colour = colour;
        this.symbol = symbol;
        this.material = material;
    }

    /**
     * The band a confidence falls in.
     *
     * @param confidence 0 - 100
     * @return the band whose floor it reaches
     * @since 1.0.0
     */
    public static Severity of(int confidence) {
        for (Severity severity : values()) {
            if (confidence >= severity.floor) {
                return severity;
            }
        }
        return WEAK;
    }

    /**
     * The lowest confidence in this band.
     *
     * @return the floor, inclusive
     * @since 1.0.0
     */
    public int getFloor() {
        return this.floor;
    }

    /**
     * The key this band uses in the configuration files.
     *
     * @return {@code critical}, {@code strong}, {@code worth-a-look} or {@code weak}
     * @since 1.0.0
     */
    public String getKey() {
        return this.key;
    }

    /**
     * The default colour, as a MiniMessage tag.
     *
     * @return the tag
     * @since 1.0.0
     */
    public String getColour() {
        return this.colour;
    }

    /**
     * The default symbol shown before an alert.
     *
     * @return the symbol
     * @since 1.0.0
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * The default menu icon.
     *
     * @return a Bukkit material name
     * @since 1.0.0
     */
    public String getMaterial() {
        return this.material;
    }
}
