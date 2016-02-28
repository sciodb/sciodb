package org.sciodb.server;

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

    public static void main(String[] args) throws Exception {
        final Runnable scioServer = new ServerSocket("127.0.0.1", 9090);

        new Thread(scioServer).start();
    }

}
