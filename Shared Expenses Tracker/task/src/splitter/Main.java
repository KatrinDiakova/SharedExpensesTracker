package splitter;

import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final CommandParser PARSER = new CommandParser();

    public static void main(String[] args) {
        boolean needExit = false;
        while (!needExit) {
            needExit = PARSER.parseUserInput(SCANNER.nextLine());
        }
    }
}
