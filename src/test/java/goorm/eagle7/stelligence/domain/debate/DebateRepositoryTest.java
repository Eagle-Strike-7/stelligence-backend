package goorm.eagle7.stelligence.domain.debate;

import static org.assertj.core.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

		// 페치 조인 잘 되었는지 테스트
		assertThat(AopUtils.isAopProxy(findDebate.getContribute())).isFalse();
		assertThat(AopUtils.isAopProxy(findDebate.getContribute().getMember())).isFalse();
		assertThat(AopUtils.isAopProxy(findDebate.getContribute().getAmendments().stream().findAny())).isFalse();
	}

	@Test
	void findPageByOpenStatus() {
		Page<Debate> debatePage = debateRepository.findPageByStatus(DebateStatus.OPEN, PageRequest.of(0, 2));
		List<Debate> debates = debatePage.getContent();
		Set<Debate> debateSet = new HashSet<>(debates);

		assertThat(debates)
			.isNotEmpty()
			.hasSize(2)
			.allMatch(d -> d.getStatus().equals(DebateStatus.OPEN));

		// 중복이 없는지 테스트
		assertThat(debateSet)
			.isNotEmpty()
			.hasSize(2);
	}

	@Test
	void findPageByCloseStatus() {
		Page<Debate> debatePage = debateRepository.findPageByStatus(DebateStatus.CLOSED, PageRequest.of(0, 2));
		List<Debate> debates = debatePage.getContent();
		Set<Debate> debateSet = new HashSet<>(debates);

		assertThat(debates)
			.isNotEmpty()
			.hasSize(2)
			.allMatch(d -> d.getStatus().equals(DebateStatus.CLOSED));

		// 중복이 없는지 테스트
		assertThat(debateSet)
			.isNotEmpty()
			.hasSize(2);
	}
}