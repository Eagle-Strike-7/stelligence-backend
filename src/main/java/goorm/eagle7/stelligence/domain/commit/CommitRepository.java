package goorm.eagle7.stelligence.domain.commit;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.commit.model.Commit;

public interface CommitRepository extends JpaRepository<Commit, Long> {
}
