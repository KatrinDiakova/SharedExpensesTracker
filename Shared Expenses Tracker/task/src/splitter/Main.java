package splitter;
/*
 * [date] borrow PersonOne PersonTwo amount
 * [date] repay PersonOne PersonTwo amount
 * [date] balancePerfect [open|close]
 * [date] balance [open|close] [(list of [+|-] persons | GROUPS)]
 * group [create|add|remove|show] GROUPNAME [(list of [+|-] persons | GROUPS)]
 * [date] purchase Person itemName amount [(list of [+|-] persons | GROUPS)]
 * [date] cashback Person itemName amount [(list of [+|-] persons | GROUPS)]
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Main implements CommandLineRunner {

    private final Scanner SCANNER = new Scanner(System.in);
    private final CommandParser commandParser;

    @Autowired
    public Main(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    @Override
    public void run(String... args) {
        boolean needExit = false;
        while (!needExit) {
            needExit = commandParser.parseUserInput(SCANNER.nextLine());
        }
    }
}