package goorm.eagle7.stelligence.domain.debate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.eagle7.stelligence.domain.debate.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("select c from Comment c"
		+ " join fetch c.commenter m"
		+ " where c.debate.id = :debateId"
		+ " order by c.createdAt asc")
	List<Comment> findAllByDebateId(@Param("debateId") Long debateId);
}
