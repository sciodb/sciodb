package org.sciodb.utils;

/**
 * Created by jesusnavarrete on 20/11/2016.
 */
public class StringUtils {

    public static boolean isInteger(final String str) {
        return str != null && str.trim().matches("-?\\d+");  //match a number with optional '-' and decimal.
    }

}
