package goorm.eagle7.stelligence.domain.contribute;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeSimpleResponse;
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

	@Test
	@DisplayName("querydsl로 투표 개수 잘 받아오는지 확인")
	void testFindSimpleContributePage() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ContributeSimpleResponse> contributePage = contributeRepository.findByContributeStatus(
			ContributeStatus.VOTING, pageable);

		assertThat(contributePage.getContent()).hasSize(2);
		assertThat(contributePage.getContent().get(1).getVoteSummary().getAgreeCount()).isEqualTo(3);
		assertThat(contributePage.getContent().get(1).getVoteSummary().getDisagreeCount()).isEqualTo(1);
	}

	@Test
	@DisplayName("querydsl로 투표 완료된 수정요청 목록 잘 받아오는지 확인")
	void testFindCompleteContributes() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ContributeSimpleResponse> contributePage = contributeRepository.findCompleteContributes(pageable);

		assertThat(contributePage.getContent()).hasSize(7);
		assertThat(contributePage.getContent().get(0).getContributeId()).isEqualTo(8L);
		assertThat(contributePage.getContent().get(1).getContributeTitle()).isEqualTo("contribute_title7");
	}

	@Test
	@DisplayName("querydsl로 문서 ID와 merged에 따라 수정요청 목록 잘 받아오는지 확인 - merged")
	void testFindByDocumentAndStatusMerged() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ContributeSimpleResponse> contributePage = contributeRepository.findByDocumentAndStatus(1L, true,
			pageable);

		assertThat(contributePage.getContent()).hasSize(2);
		assertThat(contributePage.getContent().get(0).getDocumentId()).isEqualTo(1L);
		assertThat(contributePage.getContent().get(0).getContributeTitle()).isEqualTo("contribute_title2");
	}

	@Test
	@DisplayName("querydsl로 문서 ID와 merged에 따라 수정요청 목록 잘 받아오는지 확인 - not merged")
	void testFindByDocumentAndStatusNotMerged() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ContributeSimpleResponse> contributePage = contributeRepository.findByDocumentAndStatus(4L, false,
			pageable);

		assertThat(contributePage.getContent()).hasSize(1);
		assertThat(contributePage.getContent().get(0).getDocumentId()).isEqualTo(4L);
		assertThat(contributePage.getContent().get(0).getContributeTitle()).isEqualTo("contribute_title8");
	}

}