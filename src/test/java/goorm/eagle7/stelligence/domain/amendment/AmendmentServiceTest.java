package goorm.eagle7.stelligence.domain.amendment;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import goorm.eagle7.stelligence.config.mockdata.WithMockData;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
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
		AmendmentRequest request = new AmendmentRequest(
			10L,
			AmendmentType.CREATE,
			Heading.H1,
			"newTitle",
			"newContent",
			3
		);

		//when
		Amendment amendment = amendmentService.processAmendment(request);

		//then
		assertThat(amendment.getType()).isEqualTo(AmendmentType.CREATE);
		assertThat(amendment.getTargetSection().getId()).isEqualTo(10L);
		assertThat(amendment.getNewSectionTitle()).isEqualTo("newTitle");
		assertThat(amendment.getCreatingOrder()).isEqualTo(3);
	}

	@Test
	@DisplayName("Amendment 생성 테스트 - update")
	void amendmentUpdateSave() {
		//given
		AmendmentRequest request = new AmendmentRequest(
			10L,
			AmendmentType.CREATE,
			Heading.H1,
			"newTitle",
			"newContent",
			null
		);

		//when
		Amendment amendment = amendmentService.processAmendment(request);

		//then
		assertThat(amendment.getType()).isEqualTo(AmendmentType.CREATE);
		assertThat(amendment.getTargetSection().getId()).isEqualTo(10L);
		assertThat(amendment.getNewSectionTitle()).isEqualTo("newTitle");
		assertThat(amendment.getCreatingOrder()).isNull();
	}

	@Test
	@DisplayName("Amendment 생성 테스트 - delete")
	void amendmentDeleteSave() {
		//given
		AmendmentRequest request = new AmendmentRequest(
			10L,
			AmendmentType.CREATE,
			null,
			null,
			null,
			null
		);

		//when
		Amendment amendment = amendmentService.processAmendment(request);

		//then
		assertThat(amendment.getType()).isEqualTo(AmendmentType.CREATE);
		assertThat(amendment.getTargetSection().getId()).isEqualTo(10L);
		assertThat(amendment.getNewSectionTitle()).isNull();
		assertThat(amendment.getCreatingOrder()).isNull();
	}

}
