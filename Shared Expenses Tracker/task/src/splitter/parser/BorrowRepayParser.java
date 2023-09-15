package splitter.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import splitter.Command;
import splitter.CommandProcessor;
import splitter.service.BorrowRepayService;
import splitter.util.DateUtil;

import java.math.*;
import java.time.LocalDate;
import java.util.*;

@Component
public class BorrowRepayParser implements CommandProcessor {

    private final BorrowRepayService borrowRepayService;

    @Autowired
    public BorrowRepayParser(BorrowRepayService borrowRepayService) {
        this.borrowRepayService = borrowRepayService;
    }

    @Override
    public List<Command> getCommand() {
        return List.of(Command.borrow, Command.repay);
    }

    @Override
    public void process(List<String> input) {
        try {
            LocalDate date = LocalDate.now();
            int commandIndex = 0;

            if (DateUtil.isDate(input.get(0))) {
                date = DateUtil.getDate(input.get(0));
                commandIndex = 1;
            }

            Command command = Command.of(input.get(commandIndex));

            if (Command.borrow == command || Command.repay == command) {
                String personOne = extractPersonOne(input, commandIndex);
                String personTwo = extractPersonTwo(input, commandIndex);
                BigDecimal amount = new BigDecimal(extractAmount(input, commandIndex)).setScale(2, RoundingMode.HALF_EVEN);
                borrowRepayService.process(date, command, amount, personOne, personTwo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Illegal command arguments");

        }
    }

    private String extractPersonOne(List<String> input, int commandIndex) {
        return input.get(commandIndex + 1);
    }

    private String extractPersonTwo(List<String> input, int commandIndex) {
        return input.get(commandIndex + 2);
    }

    private String extractAmount(List<String> input, int commandIndex) {
        return input.get(commandIndex + 3);
    }

}
