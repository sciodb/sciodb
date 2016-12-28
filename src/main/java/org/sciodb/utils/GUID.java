package org.sciodb.utils;

import java.util.UUID;

/**
 * @author Jesús Navarrete (28/11/2016)
 */
public class GUID {

    public static String get() {
        return UUID.randomUUID().toString();
    }

    public static long distance(final String source, final String target) {
        return UUID.fromString(source).getLeastSignificantBits() ^ UUID.fromString(target).getLeastSignificantBits();
    }

    public static void main(String[] args) {
        System.out.println(distance(GUID.get(), GUID.get()));
        System.out.println(distance(GUID.get(), GUID.get()));
        System.out.println(distance(GUID.get(), GUID.get()));
        System.out.println(distance(GUID.get(), GUID.get()));
    }
}
