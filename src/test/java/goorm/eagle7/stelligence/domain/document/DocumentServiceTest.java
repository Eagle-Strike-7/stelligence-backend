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
import goorm.eagle7.stelligence.domain.document.content.DocumentContentService;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentStatus;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentStatusResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.DocumentGraphService;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private DocumentContentService documentContentService;

	@Mock
	private DocumentGraphService documentGraphService;

	@Mock
	private DocumentRequestValidator documentRequestValidator;

	@Mock
	private ContributeRepository contributeRepository;

	@Mock
	private DebateRepository debateRepository;

	@InjectMocks
	private DocumentService documentService;

	@Test
	void createDocument() {
		//given
		Member author = member(1L, "testNickname");

		Document document = document(1L, author, "title", 1L);

		DocumentCreateRequest documentCreateRequest = DocumentCreateRequest.of(
			"testTitle",
			null,
			"testSectionContent"
		);

		when(memberRepository.findById(author.getId())).thenReturn(Optional.of(author));
		when(documentContentService.createDocument("testTitle", "testSectionContent", null, author))
			.thenReturn(document);

		//when
		documentService.createDocument(
			documentCreateRequest,
			author.getId()
		);

		//then
		//memberRepository, documentContentService, documentGraphService가 각각 한 번씩 호출되어야 합니다.
		verify(memberRepository, times(1)).findById(author.getId());
		verify(documentContentService, times(1)).createDocument("testTitle", "testSectionContent", null, author);
		verify(documentGraphService, times(1)).createDocumentNode(document);

		//기여한 글 개수가 올라야 합니다.
		assertThat(author.getContributes()).isEqualTo(1);
	}

	@Test
	@DisplayName("Document 내용 조회 - 최신버전")
	void getRecentDocumentContent() {
		//when
		documentService.getDocumentContent(1L, null);

		//then
		verify(documentContentService, times(1)).getDocument(1L);
	}

	@Test
	@DisplayName("Document 내용 조회 - 특정 버전")
	void getSpecificDocumentContent() {
		//when
		documentService.getDocumentContent(1L, 1L);

		//then
		verify(documentContentService, times(1)).getDocument(1L, 1L);
	}

	@Test
	@DisplayName("graph 조회 - 최상위 문서")
	void getDocumentGraphRoot() {
		//when
		documentService.getDocumentGraph(null, 3);

		//then
		verify(documentGraphService, times(1)).findFromRootNodesWithDepth(3);
	}

	@Test
	@DisplayName("graph 조회 - 특정 문서")
	void getDocumentGraphSpecific() {
		//when
		documentService.getDocumentGraph(1L, 3);

		//then
		verify(documentGraphService, times(1)).findGraphWithDepth(1L, 3);
	}

	@Test
	@DisplayName("문서 제목 변경")
	void changeDocumentTitle() {
		//given
		Long documentId = 1L;
		String updateTitle = "updateTitle";

		// when
		documentService.changeDocumentTitle(documentId, updateTitle);

		//then
		verify(documentContentService, times(1)).changeTitle(documentId, updateTitle);
		verify(documentGraphService, times(1)).changeTitle(documentId, updateTitle);
	}

	@Test
	@DisplayName("상위 문서 변경")
	void changeParentDocument() {
		//given
		Long documentId = 1L;
		Long parentDocumentId = 2L;

		// when
		documentService.changeParentDocument(documentId, parentDocumentId);

		//then
		verify(documentContentService, times(1)).updateParentDocument(documentId, parentDocumentId);
		verify(documentGraphService, times(1)).updateDocumentLink(documentId, parentDocumentId);
	}

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
		when(contributeRepository.findLatestContributeByDocumentId(document.getId())).thenReturn(
			Optional.of(contribute));
		when(debateRepository.findLatestDebateByDocumentId(document.getId())).thenReturn(Optional.of(debate));

		DocumentStatusResponse documentStatus = documentService.getDocumentStatus(document.getId());

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
		//투표는 토론으로 이관된 상태
		when(contributeRepository.findLatestContributeByDocumentId(1L)).thenReturn(Optional.of(contribute));
		//토론이 진행중
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(debate));

		DocumentStatusResponse response = documentService.getDocumentStatus(1L);

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

		//투표는 토론으로 이관된 상태
		when(contributeRepository.findLatestContributeByDocumentId(1L)).thenReturn(Optional.of(contribute));
		//토론 종료 후 수정 대기중
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(debate));

		DocumentStatusResponse response = documentService.getDocumentStatus(1L);

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

		//토론이 진행중이지 않음
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.empty());

		//투표중인 상태
		when(contributeRepository.findLatestContributeByDocumentId(1L)).thenReturn(Optional.of(contribute));

		DocumentStatusResponse response = documentService.getDocumentStatus(1L);

		//then
		assertThat(response.getDocumentStatus()).isEqualTo(DocumentStatus.VOTING);
		assertThat(response.getContributeId()).isEqualTo(2L);
		assertThat(response.getDebateId()).isNull();
	}

}