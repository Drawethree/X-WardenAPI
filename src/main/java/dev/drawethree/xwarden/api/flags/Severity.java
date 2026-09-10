package dev.drawethree.xwarden.api.flags;

// A number on its own tells a moderator nothing. Every finding carries a band so that "78" reads as
// "strong" rather than as a licence to ban.
public enum Severity {

    CRITICAL(85, "critical", "<dark_red>", "⛔", "REDSTONE_BLOCK"),
    STRONG(70, "strong", "<red>", "⚠", "ORANGE_DYE"),
    WORTH_A_LOOK(50, "worth-a-look", "<gold>", "◆", "YELLOW_DYE"),
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

    public static Severity of(int confidence) {
        for (Severity severity : values()) {
            if (confidence >= severity.floor) {
                return severity;
            }
        }
        return WEAK;
    }

    public int getFloor() {
        return this.floor;
    }

    public String getKey() {
        return this.key;
    }

    public String getColour() {
        return this.colour;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public String getMaterial() {
        return this.material;
    }
}
