package org.sciodb.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Jesús Navarrete on (16/12/2016)
 */
public class IpDiscover {

    public static void getIp() {
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                final NetworkInterface ni = interfaces.nextElement();
                final Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();
                    System.out.print(address.getHostAddress());
                    if (address.isLoopbackAddress()) {
                        System.out.print(" - loopback");
                    } else if (address.isSiteLocalAddress()) {
                        System.out.print(" - site local ");
                    }
                    System.out.println("");

                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
//        final String ip = Inet4Address.getLocalHost().getHostAddress();
//        "0.0.0.0";
    }

    public static void main(String[] args) {
        IpDiscover.getIp();

    }
}
