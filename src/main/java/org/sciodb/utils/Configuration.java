package org.sciodb.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jesus.navarrete  (24/02/16)
 */
public class Configuration {

    final private static Logger logger = Logger.getLogger(Configuration.class);

    final private static String PROPERTIES_FILE = "sciodb-application.properties";
    final private Properties properties;

    private static Configuration instance;

    public Configuration() {
        properties = new Properties();

        try {
            final InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);

            properties.load(input);

        } catch (IOException ex) {
            logger.error("Error reading properties file !!!", ex);
        }
    }

    public static Configuration getInstance() {
        if (instance == null) { instance = new Configuration();}
        return instance;
    }

    public int getPort() {
        return Integer.valueOf(properties.getProperty("database.port"));
    }

    public String getHost() {
        return properties.getProperty("database.host");
    }

    public String getTempFolder() {
        return properties.getProperty("temp.folder");
    }


    // Properties for Nessy Topology

    public String getRoleNessyTopology() {
        return properties.getProperty("topology.nessy.role");
    }

    public String getRootHostNessyTopology() {
        return properties.getProperty("topology.nessy.root_host");
    }

    public int getRootPortNessyTopology() {
        return Integer.valueOf(properties.getProperty("topology.nessy.root_port"));
    }

    public int getMasterCheckTimeNessyTopology() {
        return Integer.valueOf(properties.getProperty("topology.nessy.master_check.time"));
    }

    public int getNodesCheckTimeNessyTopology() {
        return Integer.valueOf(properties.getProperty("topology.nessy.nodes_check.time"));
    }

    public int getNodesPersistTimeNessyTopology() {
        return Integer.valueOf(properties.getProperty("topology.nessy.nodes_persist.time"));
    }


    @Deprecated
    public String getValue(final String property) {
        return properties.getProperty(property);
    }

    @Deprecated
    public int getInt(final String property) {
        return Integer.valueOf(properties.getProperty(property));
    }

}
