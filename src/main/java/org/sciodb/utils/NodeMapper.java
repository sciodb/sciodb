package org.sciodb.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.sciodb.messages.impl.Node;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author Jesús Navarrete (02/10/15)
 */
public class NodeMapper {

    public static String toString(final List<Node> nodes) throws IOException {
        return fromObject(nodes);
    }

    public static List<Node> fromString(final String input) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory factory = mapper.getTypeFactory();
        return mapper.readValue(input, factory.constructCollectionType(List.class, Node.class));
    }


    public static String toJson(final Node node) throws IOException {
        return fromObject(node);
    }

    public static Node toNode(final String json) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        return  mapper.readValue(json, Node.class);
    }

    private static String fromObject(final Object obj) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final StringWriter sw = new StringWriter();
        final PrintWriter p = new PrintWriter(sw);

        mapper.writeValue(p, obj);

        return sw.toString();
    }
}
