package goorm.eagle7.stelligence.domain.contribute;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;

@DataJpaTest
@WithMockData
class ContributeRepositoryTest {

	@Autowired
	private ContributeRepository contributeRepository;

	@Test
	@DisplayName("특정 제목을 가진 투표중인 수정요청이 존재하는지 여부 확인")
	void contributeDocumentTitleDuplicate() {

		boolean isExist = contributeRepository.existsDuplicateRequestedDocumentTitle("new_title_2");

		assertThat(isExist).isTrue();
	}

	@Test
	@DisplayName("특정 제목을 가진 진행중인 토론이 존재하는지 여부 확인")
	void debateDocumentTitleDuplicate() {

		boolean isExist = contributeRepository.existsDuplicateRequestedDocumentTitle("new_title_3");

		assertThat(isExist).isTrue();
	}
}