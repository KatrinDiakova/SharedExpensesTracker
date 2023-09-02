package splitter.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name")
    private String memberName;

    @ManyToMany(mappedBy = "members")
    private List<Groups> groups = new ArrayList<>();

    public Members() {}

    public Members(String memberName) {
        this.memberName = memberName;
    }
}
