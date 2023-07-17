package splitter;

import java.util.*;

public class GroupsHolder {
    private static final GroupsHolder instance = new GroupsHolder();

    private final Map<String, List<String>> groupMembers = new HashMap<>();
    private Set<String> finalListNames = new HashSet<>();

    private GroupsHolder() {
    }

    public static GroupsHolder getInstance() {
        return instance;
    }

    public Map<String, List<String>> getGroupMembers() {
        return groupMembers;
    }

    public Set<String> getFinalListNames() {
        return finalListNames;
    }

    public void setFinalListNames(Set<String> finalListNames) {
        this.finalListNames = finalListNames;
    }
}
