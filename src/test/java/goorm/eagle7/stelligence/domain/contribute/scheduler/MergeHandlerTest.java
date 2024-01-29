package goorm.eagle7.stelligence.domain.contribute.scheduler;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.amendment.model.Amendment;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.contribute.scheduler.template.AmendmentMergeTemplateMapper;
import goorm.eagle7.stelligence.domain.contribute.scheduler.template.CreateAmendmentMergeTemplate;
import goorm.eagle7.stelligence.domain.contribute.scheduler.template.DeleteAmendmentMergeTemplate;
import goorm.eagle7.stelligence.domain.contribute.scheduler.template.UpdateAmendmentMergeTemplate;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

@ExtendWith(MockitoExtension.class)
class MergeHandlerTest {

	@Mock
	ContributeRepository contributeRepository;

	@Mock
	CreateAmendmentMergeTemplate createAmendmentMergeTemplate;

	@Mock
	UpdateAmendmentMergeTemplate updateAmendmentMergeTemplate;

	@Mock
	DeleteAmendmentMergeTemplate deleteAmendmentMergeTemplate;

	@Mock
	AmendmentMergeTemplateMapper amendmentMergeTemplateMapper;

	@InjectMocks
	MergeHandler mergeHandler;

	@Test
	@DisplayName("Update와 Delete 타입의 Amendment 병합")
	void handle() {
		//given
		Member member = member(1L, "pete");

		Document document = document(1L, member, "title", 1L);
		Section s1 = section(1L, 1L, document, Heading.H1, "title", "content", 1);
		Section s2 = section(2L, 1L, document, Heading.H2, "title", "content", 2);

		Contribute contribute = contribute(1L, member, ContributeStatus.VOTING, document);

		Amendment a1 = amendment(1L, contribute, AmendmentType.UPDATE, s1, Heading.H1, "new title",
			"new content", 0);
		Amendment a2 = amendment(2L, contribute, AmendmentType.DELETE, s2, null, null, null, 0);

		//when
		when(amendmentMergeTemplateMapper.getTemplateForType(AmendmentType.UPDATE))
			.thenReturn(updateAmendmentMergeTemplate);
		when(amendmentMergeTemplateMapper.getTemplateForType(AmendmentType.DELETE))
			.thenReturn(deleteAmendmentMergeTemplate);
		when(contributeRepository.findByIdWithAmendmentsAndMember(contribute.getId())).thenReturn(
			java.util.Optional.of(contribute));

		mergeHandler.handle(contribute.getId());

		//then

		//버전이 2로 증가했는지 확인
		assertThat(document.getCurrentRevision()).isEqualTo(2);

		//contribute 상태가 MERGED로 변경되었는지 확인
		assertThat(contribute.getStatus()).isEqualTo(ContributeStatus.MERGED);

		//amentmentMergeTemplateMapper가 handle 메서드를 각각 1번씩 호출했는지 확인
		verify(amendmentMergeTemplateMapper, times(1)).getTemplateForType(AmendmentType.UPDATE);
		verify(amendmentMergeTemplateMapper, times(1)).getTemplateForType(AmendmentType.DELETE);

		verify(updateAmendmentMergeTemplate, times(1)).handle(document, a1);
		verify(deleteAmendmentMergeTemplate, times(1)).handle(document, a2);
	}

	@Test
	@DisplayName("CREATE Amendment 병합 시 순서 테스트")
	void orderTest() {
		//given
		Member member = member(1L, "pete");

		Document document = document(1L, member, "title", 1L);
		Section s1 = section(1L, 1L, document, Heading.H1, "title", "content", 2);
		Section s2 = section(2L, 1L, document, Heading.H2, "title", "content", 1);

		Contribute contribute = contribute(1L, member, ContributeStatus.VOTING, document);

		Amendment a1 = amendment(1L, contribute, AmendmentType.CREATE, s1, Heading.H1, "new title1",
			"new content1", 2);
		Amendment a2 = amendment(2L, contribute, AmendmentType.CREATE, s1, Heading.H2, "new title2", "new content2", 3);
		Amendment a3 = amendment(3L, contribute, AmendmentType.CREATE, s1, Heading.H2, "new title3", "new content3", 1);
		Amendment a4 = amendment(4L, contribute, AmendmentType.CREATE, s2, Heading.H2, "new title4", "new content4", 2);
		Amendment a5 = amendment(5L, contribute, AmendmentType.CREATE, s2, Heading.H2, "new title5", "new content5", 1);
		//when
		when(amendmentMergeTemplateMapper.getTemplateForType(AmendmentType.CREATE))
			.thenReturn(createAmendmentMergeTemplate);
		when(contributeRepository.findByIdWithAmendmentsAndMember(contribute.getId())).thenReturn(
			java.util.Optional.of(contribute));

		mergeHandler.handle(contribute.getId());

		//then

		//CREATE 내에서는 targetSection의 order 의 오름차순으로 수행되어야 함
		//targetSection이 같다면 creatingOrder의 오름차순으로 수행되어야 함
		InOrder inOrder = inOrder(createAmendmentMergeTemplate);
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a5);
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a4);
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a3);
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a1);
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a2);
	}

	@Test
	@DisplayName("CREATE와 UPDATE DELETE 섞여있는 병합 시 순서 테스트")
	void orderTest2() {
		//given
		Member member = member(1L, "pete");

		Document document = document(1L, member, "title", 1L);
		Section s1 = section(1L, 1L, document, Heading.H1, "title", "content", 1);
		Section s2 = section(2L, 1L, document, Heading.H2, "title", "content", 2);

		Contribute contribute = contribute(1L, member, ContributeStatus.VOTING, document);

		Amendment a1 = amendment(1L, contribute, AmendmentType.UPDATE, s1, Heading.H1, "new title1",
			"new content1", 0);
		Amendment a2 = amendment(2L, contribute, AmendmentType.DELETE, s1, null, null, null,
			0);
		Amendment a3 = amendment(3L, contribute, AmendmentType.UPDATE, s1, Heading.H2, "new title3", "new content3",
			0);
		Amendment a4 = amendment(4L, contribute, AmendmentType.CREATE, s2, Heading.H2, "new title4", "new content4", 2);
		Amendment a5 = amendment(5L, contribute, AmendmentType.CREATE, s2, Heading.H2, "new title5", "new content5", 1);
		//when
		when(amendmentMergeTemplateMapper.getTemplateForType(AmendmentType.CREATE))
			.thenReturn(createAmendmentMergeTemplate);
		when(amendmentMergeTemplateMapper.getTemplateForType(AmendmentType.UPDATE))
			.thenReturn(updateAmendmentMergeTemplate);
		when(amendmentMergeTemplateMapper.getTemplateForType(AmendmentType.DELETE))
			.thenReturn(deleteAmendmentMergeTemplate);
		when(contributeRepository.findByIdWithAmendmentsAndMember(contribute.getId())).thenReturn(
			java.util.Optional.of(contribute));

		mergeHandler.handle(contribute.getId());

		//then
		InOrder inOrder = inOrder(createAmendmentMergeTemplate, updateAmendmentMergeTemplate,
			deleteAmendmentMergeTemplate);
		//CREATE 타입이 먼저 수행되어야 함
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a5);
		inOrder.verify(createAmendmentMergeTemplate).handle(document, a4);

		//DELETE와 UPDATE는 순서는 상관 없으나, CREATE가 끝난 후에 수행되어야 함
		inOrder.verify(updateAmendmentMergeTemplate).handle(document, a1);
		inOrder.verify(deleteAmendmentMergeTemplate).handle(document, a2);
		inOrder.verify(updateAmendmentMergeTemplate).handle(document, a3);
	}
}