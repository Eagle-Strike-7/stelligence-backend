package goorm.eagle7.stelligence.domain.amendment;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmenCreateSavetRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentDeleteSaveRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentResponse;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentUpdateSaveRequest;
import goorm.eagle7.stelligence.domain.amendment.dto.UpdateAmendmentRequest;
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
		AmendmenCreateSavetRequest request = new AmendmenCreateSavetRequest(
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
		AmendmentUpdateSaveRequest request = new AmendmentUpdateSaveRequest(
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
		AmendmentDeleteSaveRequest request = new AmendmentDeleteSaveRequest(
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
		AmendmenCreateSavetRequest request = new AmendmenCreateSavetRequest(
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
		AmendmenCreateSavetRequest request = new AmendmenCreateSavetRequest(
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

		assertThrows(BaseException.class, () -> {
			amendmentService.deleteAmendment(response.getAmendmentId(), 3L);
		});
	}

	@Test
	@DisplayName("Amendment 삭제 테스트 - 실패: REQUESTED 상태")
	void deleteAmendmentFailWhenRequested() {
		AmendmentResponse amendment = amendmentService.getAmendment(1L);

		assertThrows(BaseException.class, () -> {
			amendmentService.deleteAmendment(amendment.getAmendmentId(), 1L);
		});
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
		List<AmendmentResponse> amendments = amendmentService.getAmendments(1L,
			1L, AmendmentStatus.PENDING);

		assertThat(amendments).hasSize(1);

		assertThat(amendments.get(0).getTitle()).isEqualTo("amendment_title4");
		assertThat(amendments.get(0).getTargetSection().getSectionId()).isEqualTo(13L);

	}

	@Test
	@DisplayName("Amendment 목록 조회 - 2가지 파라미터 존재")
	void getAmendmentsWithTwoParameters() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(1L,
			null, AmendmentStatus.REQUESTED);

		assertThat(amendments).hasSize(4);

		assertThat(amendments.get(0).getTitle()).isEqualTo("amendment_title1");
		assertThat(amendments.get(0).getTargetSection().getSectionId()).isEqualTo(2L);

		assertThat(amendments.get(1).getTitle()).isEqualTo("amendment_title2");
		assertThat(amendments.get(1).getTargetSection().getSectionId()).isEqualTo(3L);

		assertThat(amendments.get(2).getTitle()).isEqualTo("amendment_title3");
		assertThat(amendments.get(2).getTargetSection().getSectionId()).isEqualTo(1L);

		assertThat(amendments.get(3).getTitle()).isEqualTo("amendment_title5");
		assertThat(amendments.get(3).getTargetSection().getSectionId()).isEqualTo(3L);
	}

	@Test
	@DisplayName("Amendment 목록 조회 - 1가지 파라미터 존재")
	void getAmendmentsWithOneParameter() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(null,
			1L, null);

		assertThat(amendments).hasSize(5);

		// amendments.forEach(amendment -> {
		// 	assertThat(amendment.getTargetSection().getDocument().getId()).isEqualTo(1L);
		// });
	}

	@Test
	@DisplayName("모든 Amendment 목록 조회 - 파라미터 x")
	void getAllAmendments() {
		List<AmendmentResponse> amendments = amendmentService.getAmendments(null,
			null, null);

		assertThat(amendments).hasSize(7);

		assertThat(amendments.get(0).getTitle()).isEqualTo("amendment_title1");
		assertThat(amendments.get(0).getTargetSection().getSectionId()).isEqualTo(2L);

		assertThat(amendments.get(1).getTitle()).isEqualTo("amendment_title2");
		assertThat(amendments.get(1).getTargetSection().getSectionId()).isEqualTo(3L);

		assertThat(amendments.get(2).getTitle()).isEqualTo("amendment_title3");
		assertThat(amendments.get(2).getTargetSection().getSectionId()).isEqualTo(1L);

		assertThat(amendments.get(3).getTitle()).isEqualTo("amendment_title4");
		assertThat(amendments.get(3).getTargetSection().getSectionId()).isEqualTo(13L);

		assertThat(amendments.get(4).getTitle()).isEqualTo("amendment_title5");
		assertThat(amendments.get(4).getTargetSection().getSectionId()).isEqualTo(3L);

		assertThat(amendments.get(5).getTitle()).isEqualTo("amendment_title6");
		assertThat(amendments.get(5).getTargetSection().getSectionId()).isEqualTo(4L);

		assertThat(amendments.get(6).getTitle()).isEqualTo("amendment_title6");
		assertThat(amendments.get(6).getTargetSection().getSectionId()).isEqualTo(6L);
	}

	@Test
	@DisplayName("Amendment 수정 - 성공")
	void updateAmendmentSuccess() {
		UpdateAmendmentRequest request = new UpdateAmendmentRequest(
			"updateTitle",
			"updateDescription",
			Heading.H1,
			"newTitle",
			"newContent"
		);
		AmendmentResponse amendment = amendmentService.updateAmendment(4L, 1L, request);

		assertThat(amendment.getTitle()).isEqualTo("updateTitle");
		assertThat(amendment.getRequestedSectionTitle()).isEqualTo("newTitle");
		assertThat(amendment.getTargetSection().getSectionId()).isEqualTo(13L);

	}

	@Test
	@DisplayName("Amendment 수정 - 실패: status 불일치")
	void updateAmendmentFail1() {
		UpdateAmendmentRequest request = new UpdateAmendmentRequest(
			"updateTitle",
			"updateDescription",
			Heading.H1,
			"newTitle",
			"newContent"
		);

		assertThrows(BaseException.class, () -> {
			amendmentService.updateAmendment(1L, 1L, request);
		});
	}

	@Test
	@DisplayName("Amendment 수정 - 실패: 작성자 불일치")
	void updateAmendmentFail2() {
		UpdateAmendmentRequest request = new UpdateAmendmentRequest(
			"updateTitle",
			"updateDescription",
			Heading.H1,
			"newTitle",
			"newContent"
		);

		assertThrows(BaseException.class, () -> {
			amendmentService.updateAmendment(1L, 2L, request);
		});
	}

}
