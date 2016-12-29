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

    public static void sleepMaximun(final int time, final TopologyContainer container) {
        int interval = 100;

        int counter = 0;
        while (counter <= time && container.isNetworkUpdated()) {
            ThreadUtils.sleep(interval);
            counter += interval;
        }
        System.out.println("---- " + container.isNetworkUpdated());
    }

    public static void main(String[] args) {
        sleepMaximun(2000, TopologyContainer.getInstance());
    }
}
