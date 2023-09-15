package splitter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitter.entity.Groups;

import java.util.Optional;

public interface GroupsRepository extends JpaRepository<Groups, Long> {

    Optional<Groups> findByGroupName(String groupName);

    boolean existsByGroupName(String groupName);
}
