package splitter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitter.entity.Members;

import java.util.Optional;

public interface MembersRepository extends JpaRepository<Members, Long> {
    Optional<Members> findByMemberName(String name);
}
