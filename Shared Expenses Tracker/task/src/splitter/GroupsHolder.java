package splitter;

import java.util.*;

public class GroupsHolder {
    private static final GroupsHolder instance = new GroupsHolder();

    private final Map<String, Set<String>> groupMembers = new HashMap<>();
    private final Set<String> finalListNames = new HashSet<>();

    private GroupsHolder() {
    }

    public static GroupsHolder getInstance() {
        return instance;
    }

    public Map<String, Set<String>> getGroupMembers() {
        return groupMembers;
    }

    public Set<String> getFinalListNames() {
        return finalListNames;
    }
}
