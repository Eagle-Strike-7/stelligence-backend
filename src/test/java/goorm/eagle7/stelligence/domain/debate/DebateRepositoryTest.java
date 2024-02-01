package goorm.eagle7.stelligence.domain.debate;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;

@DataJpaTest
@WithMockData
class DebateRepositoryTest {

	@Autowired
	private DebateRepository debateRepository;

	@Test
	void findByIdWithContribute() {
		Long debateId = 1L;
		Debate findDebate = debateRepository.findByIdWithContribute(debateId).get();

		assertThat(findDebate.getId()).isEqualTo(debateId);
		assertThat(AopUtils.isAopProxy(findDebate.getContribute())).isFalse();
		assertThat(AopUtils.isAopProxy(findDebate.getContribute().getMember())).isFalse();
		assertThat(AopUtils.isAopProxy(findDebate.getContribute().getAmendments().stream().findAny())).isFalse();
	}

	@Test
	void findPageByOpenStatus() {
		Page<Debate> debatePage = debateRepository.findPageByStatus(DebateStatus.OPEN, PageRequest.of(0, 2));
		List<Debate> debates = debatePage.getContent();

		assertThat(debates)
			.isNotEmpty()
			.hasSize(2)
			.allMatch(d -> d.getStatus().equals(DebateStatus.OPEN));
	}

	@Test
	void findPageByCloseStatus() {
		Page<Debate> debatePage = debateRepository.findPageByStatus(DebateStatus.CLOSED, PageRequest.of(0, 2));
		List<Debate> debates = debatePage.getContent();

		assertThat(debates)
			.isNotEmpty()
			.hasSize(2)
			.allMatch(d -> d.getStatus().equals(DebateStatus.CLOSED));
	}
}