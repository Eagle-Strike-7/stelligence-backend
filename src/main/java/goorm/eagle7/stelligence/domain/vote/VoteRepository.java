package goorm.eagle7.stelligence.domain.vote;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.vote.custom.VoteCustomRepository;
import goorm.eagle7.stelligence.domain.vote.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>, VoteCustomRepository {
	Optional<Vote> findByMemberAndContribute(Member member, Contribute contribute);
}
