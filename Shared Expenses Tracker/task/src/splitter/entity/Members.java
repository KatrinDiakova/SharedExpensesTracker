package splitter.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Members")
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name", unique = true)
    private String memberName;

    @ManyToMany(mappedBy = "members")
    private Set<Groups> groups = new HashSet<>();

    public Members() {}

    public Members(String memberName) {
        this.memberName = memberName;
    }

    public Set<Groups> getGroups() { // получишь набор групп, в которых состоит member
        return groups;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String name) {
    }
}
