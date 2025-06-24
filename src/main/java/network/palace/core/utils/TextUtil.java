package network.palace.core.utils;

import java.text.DecimalFormat;

/**
 * Utility class for manipulating and formatting text in various ways.
 * Provides methods for adding grammatical pluralization and formatting numbers with commas.
 */
public class TextUtil {

    /**
     * Returns the appropriate suffix to pluralize a word based on the given count.
     * If the count is 1, no suffix is returned (empty string). Otherwise, it returns "s".
     *
     * @param count the number used to determine whether to pluralize or not
     * @return an empty string if the count is 1, or "s" otherwise
     */
    public static String pluralize(int count) {
        if (count == 1) {
            return "";
        } else {
            return "s";
        }
    }

    /**
     * Formats a given integer by adding commas as thousand separators.
     *
     * This method converts the provided number into a string representation
     * with commas to improve readability for large numbers.
     *
     * @param number the integer to be formatted with commas
     * @return a string representation of the number with commas as thousand separators
     */
    public static String addCommas(int number) {
        return new DecimalFormat("#,###").format(number);
    }

    /**
     * Formats a given double number by adding commas as thousand separators and
     * ensuring a fixed-point notation with two decimal places.
     *
     * @param number the double value to be formatted with commas and two decimal places
     * @return a string representation of the number formatted with commas as thousand
     *         separators and two decimal places
     */
    public static String addCommas(double number) {
        return new DecimalFormat("#,###.00").format(number);
    }
}
