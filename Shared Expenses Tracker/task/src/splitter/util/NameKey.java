package splitter.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record NameKey(String personOne, String personTwo) {

    public static boolean isKeyEquals(String personOne, String personTwo) {
        String sortedKey = Stream.of(personOne, personTwo)
                .sorted()
                .collect(Collectors.joining());
        String inputKey = personOne + personTwo;
        return sortedKey.equals(inputKey);
    }
}
