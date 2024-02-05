package goorm.eagle7.stelligence.domain.debate.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.debate.model.Comment;
import goorm.eagle7.stelligence.domain.debate.repository.CommentRepository;

@DataJpaTest
@WithMockData
class CommentRepositoryTest {

	@Autowired
	private CommentRepository commentRepository;

	@Test
	void findAllByDebateId() {
		Long debateId = 1L;

		List<Comment> comments = commentRepository.findAllByDebateId(debateId);

		assertThat(comments)
			.isNotEmpty()
			.hasSize(3)
			.allMatch(c -> c.getDebate().getId().equals(debateId));
	}

}