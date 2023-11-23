package org.sciodb.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jesús Navarrete (24/02/16)
 */
public class Configuration {

    final private static Logger logger = LogManager.getLogger(Configuration.class);

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
        return Integer.parseInt(properties.getProperty("database.port"));
    }

    public String getTempFolder() {
        return properties.getProperty("temp.folder");
    }

    public int getRetryTimeTopology() {
        return Integer.parseInt(properties.getProperty("topology.retry.time"));
    }

    public int getNodesCheckTimeTopology() {
        return Integer.parseInt(properties.getProperty("topology.nodes_check.time"));
    }

    public int getNodesPersistTimeTopology() {
        return Integer.parseInt(properties.getProperty("topology.nodes_persist.time"));
    }

}
