package goorm.eagle7.stelligence.domain.document;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.contribute.model.Contribute;
import goorm.eagle7.stelligence.domain.contribute.model.ContributeStatus;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentStatus;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentStatusResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

@ExtendWith(MockitoExtension.class)
class DocumentStatusServiceTest {

	@Mock
	DocumentContentRepository documentContentRepository;

	@Mock
	ContributeRepository contributeRepository;

	@Mock
	DebateRepository debateRepository;

	@InjectMocks
	DocumentStatusService documentStatusService;

	@Test
	@DisplayName("수정 가능 - 수정요청 대기 상태 지남")
	void findEditableDocument() {
		//given
		Document document = document(1L, member(1L, "hello"), "title11", 1L);

		Section s1 = section(1L, 1L, document, Heading.H1, "title1", "content1", 1);
		Contribute contribute = contribute(2L, null, "title", "description", ContributeStatus.DEBATING, document);
		Debate debate = debate(3L, contribute, DebateStatus.CLOSED,
			LocalDateTime.now().minusMinutes(Debate.DEBATE_PENDING_DURATION_MINUTE).minusMinutes(1L), 1);

		//when
		when(documentContentRepository.existsById(1L)).thenReturn(true);
		when(contributeRepository.findLatestContributeByDocumentId(document.getId())).thenReturn(
			Optional.of(contribute));
		when(debateRepository.findLatestDebateByDocumentId(document.getId())).thenReturn(Optional.of(debate));

		DocumentStatusResponse documentStatus = documentStatusService.getDocumentStatus(document.getId());

		//then
		assertThat(documentStatus.getDocumentStatus()).isEqualTo(DocumentStatus.EDITABLE);
		assertThat(documentStatus.getContributeId()).isNull();
		assertThat(documentStatus.getDebateId()).isNull();
	}

	@Test
	@DisplayName("수정 불가능 - 토론 진행중")
	void editableTestDebatesOpen() {

		//given
		Document document = document(1L, member(1L, "hello"), "title11", 1L);

		Section s1 = section(1L, 1L, document, Heading.H1, "title1", "content1", 1);
		Contribute contribute = contribute(2L, null, "title", "description", ContributeStatus.DEBATING, document);
		Debate debate = debate(3L, contribute, DebateStatus.OPEN, LocalDateTime.now().plusMinutes(30L), 1);

		//when
		when(documentContentRepository.existsById(1L)).thenReturn(true);
		//투표는 토론으로 이관된 상태
		when(contributeRepository.findLatestContributeByDocumentId(1L)).thenReturn(Optional.of(contribute));
		//토론이 진행중
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(debate));

		DocumentStatusResponse response = documentStatusService.getDocumentStatus(1L);

		//then
		assertThat(response.getDocumentStatus()).isEqualTo(DocumentStatus.DEBATING);
		assertThat(response.getContributeId()).isNull();
		assertThat(response.getDebateId()).isEqualTo(3L);
	}

	@Test
	@DisplayName("수정 불가능 - 토론 종료 후 수정 대기중")
	void editableTestDebatePending() {

		//given
		Document document = document(1L, member(1L, "hello"), "title11", 1L);

		Section s1 = section(1L, 1L, document, Heading.H1, "title1", "content1", 1);
		Contribute contribute = contribute(2L, null, "title", "description", ContributeStatus.DEBATING, document);
		Debate debate = debate(3L, contribute, DebateStatus.CLOSED, LocalDateTime.now(), 1);

		//when
		when(documentContentRepository.existsById(1L)).thenReturn(true);
		//투표는 토론으로 이관된 상태
		when(contributeRepository.findLatestContributeByDocumentId(1L)).thenReturn(Optional.of(contribute));
		//토론 종료 후 수정 대기중
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(debate));

		DocumentStatusResponse response = documentStatusService.getDocumentStatus(1L);

		//then
		assertThat(response.getDocumentStatus()).isEqualTo(DocumentStatus.PENDING);
		assertThat(response.getContributeId()).isNull();
		assertThat(response.getDebateId()).isEqualTo(3L);
	}

	@Test
	@DisplayName("수정 불가능 - 투표 진행중")
	void editableTestVoting() {

		//given
		Document document = document(1L, member(1L, "hello"), "title11", 1L);

		Section s1 = section(1L, 1L, document, Heading.H1, "title1", "content1", 1);
		Contribute contribute = contribute(2L, null, "title", "description", ContributeStatus.VOTING, document);

		//when
		when(documentContentRepository.existsById(1L)).thenReturn(true);
		//토론이 진행중이지 않음
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.empty());

		//투표중인 상태
		when(contributeRepository.findLatestContributeByDocumentId(1L)).thenReturn(Optional.of(contribute));

		DocumentStatusResponse response = documentStatusService.getDocumentStatus(1L);

		//then
		assertThat(response.getDocumentStatus()).isEqualTo(DocumentStatus.VOTING);
		assertThat(response.getContributeId()).isEqualTo(2L);
		assertThat(response.getDebateId()).isNull();
	}

}