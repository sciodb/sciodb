package org.sciodb.shell;

import java.util.Scanner;

/**
 * Created by jesusnavarrete on 12/09/16.
 */
public class ScioSchell {

    private static String prompt = "scio> ";

    public static void main(String[] args) {

        intro();
        final Scanner scanner = new Scanner(System.in);
        int exitCode = 0;
        while (exitCode >= 0) {
            System.out.print(prompt);
            final String str = scanner.nextLine();
            if (str != null && !"".equals(str)) {
                exitCode = parser(str);
            }
        }
    }
    /*
    * reserved words:
     * help, exit/quit, version
     * show ???,
    *
    */
    public static int parser(final String str) {
        String input = str.trim().replace("\\s+", " ");
        if ("help".equals(input)) {
            return help();
        } else if ("exit".equals(input)) {
            return -1;
        } else if("version".equals(input)) {
            return intro();
        } else if ("show databases".equals(input) || "show databases;".equals(input)) {
            System.out.println("- system");
            System.out.println("- temp");
            return 1;
        } else {
            System.out.println("What do you mean with \"" + str + "\" ?");
            return 0;
        }

    }

    public static int intro() {
        System.out.println("ScioDB - Command Line utility");
        System.out.println("v. 0.1");
        System.out.println("Type ");
        return 0;
    }

    public static int help() {
        System.out.println(" help \t- print this message");
        System.out.println(" version \t- print the version number of the console tool and the database connected");
        return 0;
    }

    public static void p(final String input) {
        System.out.println();
    }
}
