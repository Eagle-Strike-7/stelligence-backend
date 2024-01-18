package goorm.eagle7.stelligence.domain.amendment;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentSaveCreateRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentSaveDeleteRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentSaveUpdateRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentUpdateRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentStatus;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@WithMockData
class AmendmentServiceTest {

	//데이터 given
	//수행 when
	//결과를 검증 then

	@Autowired
	AmendmentService amendmentService;

	@Autowired
	AmendmentRepository amendmentRepository;

	@Test
	@DisplayName("Amendment 생성 테스트 - create")
	void amendmentCreateSave() {
		//given
		AmendmentSaveCreateRequest request = new AmendmentSaveCreateRequest(
			"title",
			"description",
			10L,
			Heading.H1,
			"newTitle",
			"newContent"
		);

		//when
		AmendmentResponse response = amendmentService.saveCreateAmendment(request, 1L);

		//then
		assertThat(response.getType()).isEqualTo(AmendmentType.CREATE);
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getTargetSection().getSectionId()).isEqualTo(10L);

		Amendment amendment = amendmentRepository.findById(response.getAmendmentId()).get();

		assertThat(amendment.getMember().getId()).isEqualTo(1L);
		assertThat(amendment.getContribute()).isNull();
		assertThat(amendment.getNewSectionTitle()).isEqualTo("newTitle");
		assertThat(amendment.getStatus()).isEqualByComparingTo(AmendmentStatus.PENDING);
	}

	@Test
	@DisplayName("Amendment 생성 테스트 - update")
	void amendmentUpdateSave() {
		//given
		AmendmentSaveUpdateRequest request = new AmendmentSaveUpdateRequest(
			"title",
			"description",
			10L,
			Heading.H1,
			"newTitle",
			"newContent"
		);

		//when
		AmendmentResponse response = amendmentService.saveUpdateAmendment(request, 1L);

		//then
		assertThat(response.getType()).isEqualTo(AmendmentType.UPDATE);
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getTargetSection().getSectionId()).isEqualTo(10L);

		Amendment amendment = amendmentRepository.findById(response.getAmendmentId()).get();

		assertThat(amendment.getMember().getId()).isEqualTo(1L);
		assertThat(amendment.getContribute()).isNull();
		assertThat(amendment.getNewSectionTitle()).isEqualTo("newTitle");
		assertThat(amendment.getStatus()).isEqualByComparingTo(AmendmentStatus.PENDING);
	}

	@Test
	@DisplayName("Amendment 생성 테스트 - delete")
	void amendmentDeleteSave() {
		//given
		AmendmentSaveDeleteRequest request = new AmendmentSaveDeleteRequest(
			"title",
			"description",
			10L
		);

		//when
		AmendmentResponse response = amendmentService.saveDeleteAmendment(request, 1L);

		//then
		assertThat(response.getType()).isEqualTo(AmendmentType.DELETE);
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getTargetSection().getSectionId()).isEqualTo(10L);

		Amendment amendment = amendmentRepository.findById(response.getAmendmentId()).get();

		assertThat(amendment.getMember().getId()).isEqualTo(1L);
		assertThat(amendment.getContribute()).isNull();
		assertThat(amendment.getNewSectionTitle()).isNull();
		assertThat(amendment.getStatus()).isEqualByComparingTo(AmendmentStatus.PENDING);
	}

	@Test
	@DisplayName("Amendment 삭제 테스트 - 성공")
	void deleteAmendmentSuccess() {
		AmendmentSaveCreateRequest request = new AmendmentSaveCreateRequest(
			"title",
			"description",
			10L,
			Heading.H1,
			"newTitle",
			"newContent"
		);

		//when
		AmendmentResponse response = amendmentService.saveCreateAmendment(request, 1L);

		//then
		assertThat(response.getType()).isEqualTo(AmendmentType.CREATE);
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getTargetSection().getSectionId()).isEqualTo(10L);

		amendmentService.deleteAmendment(response.getAmendmentId(), 1L);

		assertThat(amendmentRepository.findById(response.getAmendmentId())).isEmpty();
	}

	@Test
	@DisplayName("Amendment 삭제 테스트 - 실패: memberId 불일치")
	void deleteAmendmentFail() {
		AmendmentSaveCreateRequest request = new AmendmentSaveCreateRequest(
			"title",
			"description",
			10L,
			Heading.H1,
			"newTitle",
			"newContent"
		);

		//when
		AmendmentResponse response = amendmentService.saveCreateAmendment(request, 1L);

		//then
		assertThat(response.getType()).isEqualTo(AmendmentType.CREATE);
		assertThat(response.getTitle()).isEqualTo("title");
		assertThat(response.getTargetSection().getSectionId()).isEqualTo(10L);

		BaseException thrown = assertThrows(BaseException.class, () -> {
			amendmentService.deleteAmendment(response.getAmendmentId(), 3L);
		});

		assertThat(thrown.getMessage()).isEqualTo("삭제 권한이 없습니다. 사용자 ID: " + 3L);
	}

	@Test
	@DisplayName("Amendment 삭제 테스트 - 실패: REQUESTED 상태")
	void deleteAmendmentFailWhenRequested() {
		AmendmentResponse amendment = amendmentService.getAmendment(1L);

		BaseException thrown = assertThrows(BaseException.class, () -> {
			amendmentService.deleteAmendment(amendment.getAmendmentId(), 1L);
		});

		assertThat(thrown.getMessage()).isEqualTo("이미 요청중인 수정안은 삭제할 수 없습니다.");
	}

	@Test
	@DisplayName("Amendment 개별 조회")
	void getAmendment() {
		AmendmentResponse amendment = amendmentService.getAmendment(1L);

		assertThat(amendment.getTitle()).isEqualTo("amendment_title1");
		assertThat(amendment.getTargetSection().getSectionId()).isEqualTo(2L);

	}

	@Test
	@DisplayName("Amendment 목록 조회 - 3가지 파라미터 존재")
	void getAmendmentsWithAllParameters() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(
			AmendmentStatus.PENDING, 1L, 1L);

		assertThat(amendments).hasSize(1);

		assertThat(amendments.get(0).getTitle()).isEqualTo("amendment_title4");
		assertThat(amendments.get(0).getTargetSection().getSectionId()).isEqualTo(13L);

	}

	@Test
	@DisplayName("Amendment 목록 조회 - 2가지 파라미터 존재")
	void getAmendmentsWithTwoParameters() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(
			AmendmentStatus.REQUESTED, null, 1L);

		assertThat(amendments).hasSize(4);

		List<String> expectedTitles = Arrays.asList("amendment_title1", "amendment_title2",
			"amendment_title3", "amendment_title5");

		List<Long> expectedSectionIds = Arrays.asList(2L, 3L, 1L, 3L);

		List<String> actualTitles = amendments.stream()
			.map(AmendmentResponse::getTitle)
			.toList();

		List<Long> actualSectionIds = amendments.stream()
			.map(amendment -> amendment.getTargetSection().getSectionId())
			.toList();

		//실제 제목, 실제 섹션id가 예상과 같은지 확인
		assertThat(actualTitles).containsAll(expectedTitles);
		assertThat(actualSectionIds).containsAll(expectedSectionIds);
	}

	@Test
	@DisplayName("Amendment 목록 조회 - 1가지 파라미터 존재")
	void getAmendmentsWithOneParameter() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(
			AmendmentStatus.PENDING, null, null);

		assertThat(amendments).hasSize(1);

		assertThat(amendments.get(0).getTitle()).isEqualTo("amendment_title4");
		assertThat(amendments.get(0).getTargetSection().getSectionId()).isEqualTo(13L);
	}

	@Test
	@DisplayName("모든 Amendment 목록 조회 - 파라미터 x")
	void getAllAmendments() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(null,
			null, null);

		assertThat(amendments).hasSize(7);

		List<String> expectedTitles = Arrays.asList("amendment_title1", "amendment_title2",
			"amendment_title3", "amendment_title4", "amendment_title5", "amendment_title6", "amendment_title6");

		List<Long> expectedSectionIds = Arrays.asList(2L, 3L, 1L, 13L, 3L, 4L, 6L);

		List<String> actualTitles = amendments.stream()
			.map(AmendmentResponse::getTitle)
			.toList();

		List<Long> actualSectionIds = amendments.stream()
			.map(amendment -> amendment.getTargetSection().getSectionId())
			.toList();

		//실제 제목, 실제 섹션id가 예상과 같은지 확인
		assertThat(actualTitles).containsAll(expectedTitles);
		assertThat(actualSectionIds).containsAll(expectedSectionIds);
	}

	@Test
	@DisplayName("Amendment 수정 - 성공")
	void updateAmendmentSuccess() {
		AmendmentUpdateRequest request = new AmendmentUpdateRequest(
			"updateTitle",
			"updateDescription",
			Heading.H1,
			"newTitle",
			"newContent"
		);
		AmendmentResponse amendment = amendmentService.updateAmendment(request, 4L, 1L);

		assertThat(amendment.getTitle()).isEqualTo("updateTitle");
		assertThat(amendment.getRequestedSectionTitle()).isEqualTo("newTitle");
		assertThat(amendment.getTargetSection().getSectionId()).isEqualTo(13L);
	}

	@Test
	@DisplayName("Amendment 수정 - 실패: status 불일치")
	void updateAmendmentFail1() {
		AmendmentUpdateRequest request = new AmendmentUpdateRequest(
			"updateTitle",
			"updateDescription",
			Heading.H1,
			"newTitle",
			"newContent"
		);

		BaseException thrown = assertThrows(BaseException.class, () -> {
			amendmentService.updateAmendment(request, 1L, 1L);
		});

		assertThat(thrown.getMessage()).isEqualTo("수정 요청 후에는 수정안을 수정할 수 없습니다.");

	}

	@Test
	@DisplayName("Amendment 수정 - 실패: 작성자 불일치")
	void updateAmendmentFail2() {
		AmendmentUpdateRequest request = new AmendmentUpdateRequest(
			"updateTitle",
			"updateDescription",
			Heading.H1,
			"newTitle",
			"newContent"
		);

		BaseException thrown = assertThrows(BaseException.class, () -> {
			amendmentService.updateAmendment(request, 1L, 2L);
		});

		assertThat(thrown.getMessage()).isEqualTo("수정 권한이 없습니다. 사용자 ID: " + 2L);
	}

}
