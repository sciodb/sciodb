package org.sciodb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sciodb.utils.models.Command;

import java.io.IOException;

/**
 * @author jesus.navarrete  (08/03/16)
 */
public class CommandEncoder {

    public static String encode(final Command input) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static Command decode(final String input) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(input, Command.class);
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String str = "";
    }
}
