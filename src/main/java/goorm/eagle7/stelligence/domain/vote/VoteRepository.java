package goorm.eagle7.stelligence.domain.vote;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.vote.custom.VoteCustomRepository;
import goorm.eagle7.stelligence.domain.vote.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>, VoteCustomRepository {
}
