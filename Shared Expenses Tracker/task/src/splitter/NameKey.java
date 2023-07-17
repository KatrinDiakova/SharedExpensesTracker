package splitter;

import java.util.Arrays;

public class NameKey {

    private final String key;
    private final String sortedKey;


    public NameKey(String personOne, String personTwo) {
        this.key = personOne + "-" + personTwo;
        String[] names = {personOne, personTwo};
        Arrays.sort(names);
        this.sortedKey = names[0] + "-" + names[1];
    }

    public String getKey() {
        return key;
    }

    public String getSortedKey() {
        return sortedKey;
    }
}
