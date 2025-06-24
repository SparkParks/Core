package network.palace.core.utils;

import org.bukkit.Location;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Utility class for mathematical operations.
 * This class provides methods to perform various mathematical calculations
 * including flooring a number, calculating its square, and rounding coordinates.
 */
public class MathUtil {

    /**
     * Returns the largest integer less than or equal to the specified double value.
     * This method performs a flooring operation to truncate the decimal part of the input
     * and adjusts based on the sign of the number.
     *
     * @param num the double value to be floored
     * @return the largest integer less than or equal to the specified double
     */
    public static int floor(double num) {
        int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    /**
     * Calculates the square of a given number.
     * The method multiplies the input value by itself to compute the square.
     *
     * @param num the number to be squared
     * @return the square of the specified number
     */
    public static double square(double num) {
        return num * num;
    }

    /**
     * Rounds the coordinates (X, Y, Z) and orientation (Yaw, Pitch) of a given location
     * to the specified number of decimal places.
     *
     * @param loc the location object whose coordinates and orientation need to be rounded
     * @param places the number of decimal places to retain during the rounding operation
     * @return the modified location object with rounded coordinates and orientation
     */
    public static Location round(Location loc, int places) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < places; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        loc.setX(Double.parseDouble(df.format(loc.getX())));
        loc.setY(Double.parseDouble(df.format(loc.getY())));
        loc.setZ(Double.parseDouble(df.format(loc.getZ())));
        loc.setYaw(Float.parseFloat(df.format(loc.getYaw())));
        loc.setPitch(Float.parseFloat(df.format(loc.getPitch())));
        return loc;
    }
}
