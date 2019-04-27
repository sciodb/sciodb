package org.sciodb.utils;

import org.apache.log4j.Logger;
import org.sciodb.messages.impl.Node;
import org.sciodb.topology.TopologyContainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Jesús Navarrete (02/10/15)
 */
public class FileUtils {

    private static final Logger logger = Logger.getLogger(FileUtils.class);

    public static final String OUTPUT_FILE = "sciodb-nodes.json";
    public static final Charset ENCODING = StandardCharsets.UTF_8;

    public static String read(final String aFileName, final Charset ENCODING) throws IOException {
        final File f = new File(aFileName);
        if (f.exists()) {
            final Path path = Paths.get(aFileName);
            final StringBuilder sb = new StringBuilder();
            try (Scanner scanner =  new Scanner(path, ENCODING.name())){
                while (scanner.hasNextLine()){
                    sb.append(scanner.nextLine());
                }
            }
            return sb.toString();
        }
        return "";
    }

    public static void write(final String fileName, final String content, final Charset ENCODING) throws
            IOException {
        final Path path = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
            writer.write(content);
        }
    }

    public static void persistNodes(final int number) {
        final List<Node> nodes = new ArrayList<>(TopologyContainer.getInstance().getAvailableNodes());

        try {
            if (nodes.size() > 0) {
                final String output = NodeMapper.toString(nodes);

                final String fileName = Configuration.getInstance().getTempFolder() + number + "_" + OUTPUT_FILE;

                FileUtils.write(fileName, output, ENCODING);
            }

        } catch (IOException e) {
            logger.error("Error persisting the nodes information", e);
        }
    }
}
