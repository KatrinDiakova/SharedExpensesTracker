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
    private final Set<Members> members = new HashSet<>();

    public Groups() {}

    public Groups(String groupName) {
        this.groupName = groupName;
    }

    public Set<Members> getMembers() {
        return members;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setMembers(Set<Members> members) {
        if (members != null) {
            this.members.clear();
            this.members.addAll(members);
        }
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

