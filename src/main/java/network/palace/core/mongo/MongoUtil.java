package network.palace.core.mongo;

/**
 * Utility class for performing string operations related to the conversion
 * of periods ('.') and commas (',').
 *
 * The class provides methods that allow for the replacement of periods with commas
 * and vice versa in a given string. These conversions can be useful in scenarios
 * where certain characters are restricted or need to be transformed, such as working
 * with databases or formatting data.
 *
 * All methods in this class are static and can be called directly without
 * the need to instantiate the class.
 */
public class MongoUtil {

    /**
     * Replaces all occurrences of periods ('.') in the input string with commas (',').
     *
     * This method is useful for transforming strings where periods need to be converted
     * to commas, such as in number or text formatting scenarios.
     *
     * @param s the input string in which periods will be replaced with commas;
     *          must not be null to avoid a {@code NullPointerException}.
     * @return a new string with all periods replaced by commas.
     */
    public static String periodToComma(String s) {
        return s.replaceAll("\\.", ",");
    }

    /**
     * Replaces all occurrences of commas (',') in the input string with periods ('.').
     *
     * This method is useful for transforming strings where commas need to be converted
     * to periods, such as in number or text formatting.
     *
     * @param s the input string in which commas will be replaced with periods;
     *          must not be null to avoid a {@code NullPointerException}.
     * @return a new string with all commas replaced by periods.
     */
    public static String commaToPeriod(String s) {
        return s.replaceAll(",", ".");
    }

//    public static String sterilizePeriod(String s) {
//        return s.replaceAll("/\\./", "\\u002e");
//    }
}
