package org.sciodb.utils;

import org.sciodb.messages.impl.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jesús Navarrete (20/11/2016)
 */
public class SeedUtils {

    public static List<Node> fromString(final String input) {
        final List<Node> seeds = new ArrayList<>();
        if (input != null) {
            final String[] seedsStr = input.split(",");

            for (final String seed : seedsStr) {
                final String[] parts = seed.split(":");
                if (parts.length == 2 && StringUtils.isInteger(parts[1]) && !parts[0].trim().equals("")) {
                    seeds.add(new Node(parts[0], Integer.parseInt(parts[1].trim())));
                }
            }
        }

        return seeds;
    }

}
