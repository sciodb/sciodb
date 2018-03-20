package org.sciodb.utils;

import org.sciodb.topology.TopologyContainer;

/**
 * @author Jesús Navarrete (13/07/16)
 */
public class ThreadUtils {

    public static void sleep(final int time) {
        try {
            Thread.sleep(time);
        } catch (final InterruptedException ie) {
            /*not important*/
        }
    }

}
