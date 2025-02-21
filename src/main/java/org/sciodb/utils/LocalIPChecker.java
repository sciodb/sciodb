package org.sciodb.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.util.Enumeration;

public class LocalIPChecker {

    private final static Logger logger = LogManager.getLogger(LocalIPChecker.class);

    public static String getIP() throws SocketException {
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            for (InterfaceAddress ia: interfaces.nextElement().getInterfaceAddresses()) {
                if (ia.getAddress().isSiteLocalAddress()) {
                    if (isLocalIP(ia.getAddress().getHostAddress())) {
                        return ia.getAddress().getHostAddress();
                    }
                }
            }
        }
        return "0.0.0.0";
    }
    public static boolean isLocalIP(String ip) {
        try {
            final InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();

            int firstByte = Byte.toUnsignedInt(bytes[0]);
            int secondByte = Byte.toUnsignedInt(bytes[1]);

            // Verify local range IPs
            if (firstByte == 10) {
                return true; // 10.0.0.0 - 10.255.255.255
            } else if (firstByte == 172 && (secondByte >= 16 && secondByte <= 31)) {
                return true; // 172.16.0.0 - 172.31.255.255
            } else if (firstByte == 192 && secondByte == 168) {
                return true; // 192.168.0.0 - 192.168.255.255
            } else if (firstByte == 169 && secondByte == 254) {
                return true; // 169.254.0.0 - 169.254.255.255 (link-local)
            }
        } catch (UnknownHostException e) {
            logger.error("local network address not valid {}", ip);
        }
        return false;
    }
}
