package goorm.eagle7.stelligence.domain.debate;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.eagle7.stelligence.domain.debate.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
