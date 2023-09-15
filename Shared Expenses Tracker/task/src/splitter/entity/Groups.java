package splitter.entity;

import javax.persistence.*;
import java.util.*;

@Entity
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name", unique = true)
    private String groupName;

    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private final List<Members> members = new ArrayList<>();

    public Groups() {}

    public Groups(String groupName) {
        this.groupName = groupName;
    }

    public List<Members> getMembers() {
        return members;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
}

