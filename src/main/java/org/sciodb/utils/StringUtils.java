package org.sciodb.utils;

/**
 * @author Jesús Navarrete (20/11/2016)
 */
public class StringUtils {

    public static boolean isInteger(final String str) {
        return str != null && str.trim().matches("-?\\d+");  //match a number with optional '-' and decimal.
    }

    public static boolean isEmpty(final String input) {
        return input == null || "".equals(input);
    }

    public static boolean isNotEmpty(final String input) {
        return !isEmpty(input);
    }
}
