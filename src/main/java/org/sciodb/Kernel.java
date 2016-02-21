package org.sciodb;

/**
 * Kernel.java - this is the point to start
 *
 * @author jesus.navarrete  (16/02/16)
 *
 * Copyright (C) 2016 Jesús Navarrete <jenaiz@alblang.org>
 *
 * This source code is licensed under the GNU General Public License,
 * Version 2.  See the file COPYING for more details.
 */
public class Kernel {

    public static void main(String[] args) {
        System.out.println("Hello world");

        final Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread());

    }

}
