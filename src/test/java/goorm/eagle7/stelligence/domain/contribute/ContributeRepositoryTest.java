package goorm.eagle7.stelligence.domain.contribute;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;

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

	@Test
	@DisplayName("[성공] 활성 멤버가 작성한 수정 요청의 총 개수를 가져온다.")
	void countDistinctByMemberIdActiveSuccess() {
		// 4번까지가 활성, 4번이 작성한 요청은 3개
		long count = contributeRepository.countByMemberId(1L);
		assertThat(count).isEqualTo(3);
	}

	@Test
	@DisplayName("[성공] 탈퇴 멤버가 작성한 수정 요청의 총 개수를 가져온다.")
	void countDistinctByMemberIdExpiredSuccess() {
		// 5번부터 탈퇴, 5번이 작성한 요청은 3개
		long count = contributeRepository.countByMemberId(5L);
		assertThat(count).isEqualTo(3);
	}

	@Test
	@DisplayName("[성공] 수정 요청을 작성한 적 없는 활성 멤버가 작성한 수정 요청의 총 개수를 가져온다.")
	void countDistinctByMemberIdActiveNoContributeSuccess() {
		// 6번이 작성한 요청은 없음
		long count = contributeRepository.countByMemberId(6L);
		assertThat(count).isZero();
	}

	@Test
	@DisplayName("[성공] 활성 멤버가 작성한 수정 요청 중 Merge의 총 개수를 가져온다.")
	void countDistinctByMemberIdAndMergedActiveSuccess() {
		// 4번이 작성한 요청 중 상태가 MERGED인 요청은 1개
		long count = contributeRepository.countByMemberIdAndStatus(1L, ContributeStatus.MERGED);
		assertThat(count).isEqualTo(1);
	}

	@Test
	@DisplayName("[성공] 수정 요청을 작성한 적 없는 활성 멤버가 작성한 수정 요청 중 Merge의 총 개수를 가져온다.")
	void countDistinctByMemberIdAndMergedActiveNoContributeSuccess() {
		// 6번이 작성한 요청 중 상태가 MERGED인 요청은 없음
		long count = contributeRepository.countByMemberIdAndStatus(6L, ContributeStatus.MERGED);
		assertThat(count).isZero();
	}

	@Test
	@DisplayName("[성공] 탈퇴 멤버가 작성한 수정 요청 중 REJECTED 총 개수를 가져온다.")
	void countDistinctByMemberIdAndRejectedExpiredSuccess() {
		// 5번이 작성한 요청 중 상태가 REJECTED인 요청은 1개
		long count = contributeRepository.countByMemberIdAndStatus(5L, ContributeStatus.REJECTED);
		assertThat(count).isEqualTo(1);
	}

}