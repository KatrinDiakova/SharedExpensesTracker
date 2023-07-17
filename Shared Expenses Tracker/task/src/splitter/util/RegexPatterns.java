package splitter.util;

import java.util.regex.Pattern;

public class RegexPatterns {

    public static final Pattern PLUS_PATTERN = Pattern.compile("(?:(?<=\\+)|(?<![-\\w]))[A-Z]+(?!\\w)", Pattern.CASE_INSENSITIVE);
    public static final Pattern MINUS_PATTERN = Pattern.compile("(?:(?<=\\-))[A-Z]+(?!\\w)", Pattern.CASE_INSENSITIVE);
    public static final Pattern GROUP_PATTERN = Pattern.compile("[A-Z]+\\b");
}
