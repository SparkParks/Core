package network.palace.core.commands;

import java.util.Arrays;

/**
 * BoilerplateUtil is a utility class providing functionality for generating formatted boilerplate text.
 */
public class BoilerplateUtil {

    /**
     * Generates a boilerplate text by appending a fixed-length padding of spaces
     * to the provided format string.
     *
     * @param format the base string to which the boilerplate padding is appended
     * @return a string consisting of the format parameter followed by the boilerplate padding
     */
    public static String getBoilerplateText(String format) {
        char[] plating = new char[64];
        Arrays.fill(plating, ' ');
        return format + new String(plating);
    }
}
