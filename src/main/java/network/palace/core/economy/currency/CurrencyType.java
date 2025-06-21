package network.palace.core.economy.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * Represents different types of currencies in the system.
 * <p>
 * Each currency type includes an icon representation and a color code for display purposes.
 * The available currency types are:
 * - BALANCE: Represented by the "$" icon and green color.
 * - TOKENS: Represented by the "✪ " icon and yellow color.
 * - ADVENTURE: Represented by the "➶" icon and green color.
 */
@AllArgsConstructor
public enum CurrencyType {
    BALANCE("$", ChatColor.GREEN), TOKENS("✪ ", ChatColor.YELLOW), ADVENTURE("➶", ChatColor.GREEN);

    /**
     * The visual representation of the currency type.
     * This is used as an icon to symbolize the specific currency,
     * such as "$" for BALANCE, "✪ " for TOKENS, or "➶" for ADVENTURE.
     */
    @Getter private String icon;

    /**
     * The color associated with the currency type.
     * This is utilized for visually distinguishing different currency types
     * in the system, corresponding to their display requirements. For example,
     * BALANCE uses green, TOKENS uses yellow, and ADVENTURE uses green.
     */
    @Getter private ChatColor color;

    /**
     * Retrieves the name of the currency type in lowercase.
     *
     * @return the lowercase representation of the currency type's name
     */
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Converts a string representation of a currency type into its corresponding {@code CurrencyType} enum.
     * The conversion is case-insensitive.
     *
     * @param s the string representation of the currency type; expected values are "balance", "tokens", or "adventure"
     * @return the corresponding {@code*/
    public static CurrencyType fromString(String s) {
        switch (s.toLowerCase()) {
            case "balance":
                return BALANCE;
            case "tokens":
                return TOKENS;
            case "adventure":
                return ADVENTURE;
            default:
                return null;
        }
    }

}
