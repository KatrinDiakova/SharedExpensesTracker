package splitter.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;

public class DateUtil {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static LocalDate getDate(String input) {
        return Optional.of(input)
                .filter(DATE_PATTERN.asPredicate())
                .map(it -> LocalDate.parse(it, FORMATTER))
                .orElse(LocalDate.now());
    }

    public static boolean isDate(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }
}
