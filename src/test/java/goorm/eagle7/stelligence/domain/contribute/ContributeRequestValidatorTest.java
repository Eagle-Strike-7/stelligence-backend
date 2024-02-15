package goorm.eagle7.stelligence.domain.contribute;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.amendment.dto.AmendmentRequest;
import goorm.eagle7.stelligence.domain.amendment.model.AmendmentType;
import goorm.eagle7.stelligence.domain.contribute.dto.ContributeRequest;
import goorm.eagle7.stelligence.domain.debate.model.Debate;
import goorm.eagle7.stelligence.domain.debate.model.DebateStatus;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;

@ExtendWith(MockitoExtension.class)
class ContributeRequestValidatorTest {

	@Mock
	SectionRepository sectionRepository;
	@Mock
	ContributeRepository contributeRepository;
	@Mock
	DocumentContentRepository documentContentRepository;
	@Mock
	DebateRepository debateRepository;
	@InjectMocks
	ContributeRequestValidator contributeRequestValidator;

	@Test
	@DisplayName("검증 성공케이스")
	void validateSuccess() {

		Document targetDocument = document(1L, member(1L, "pete"), "title", 1L, null);
		Long loginMemberId = 2L;

		AmendmentRequest a1 = new AmendmentRequest(1L, AmendmentType.DELETE, Heading.H2, "title",
			"content", 0);
		AmendmentRequest a2 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);
		AmendmentRequest a3 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 2);
		AmendmentRequest a4 = new AmendmentRequest(2L, AmendmentType.UPDATE, Heading.H2, "title",
			"content", 0);
		AmendmentRequest a5 = new AmendmentRequest(2L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);
		AmendmentRequest a6 = new AmendmentRequest(3L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);

		ContributeRequest contributeRequest = new ContributeRequest("title", "description",
			List.of(a1, a2, a3, a4, a5, a6), 1L, "title", 2L, null);

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(sectionRepository.findSectionIdByVersion(any(), any())).thenReturn(List.of(1L, 2L, 3L));
		when(documentContentRepository.findByTitle("title")).thenReturn(Optional.of(targetDocument));
		when(contributeRepository.existsDuplicateRequestedDocumentTitle("title")).thenReturn(false);
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.empty());

		//then
		assertThatNoException().isThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId));

	}

	@Test
	@DisplayName("documentId에 해당하는 문서가 존재하지 않는 경우")
	void noDocument() {
		//given
		ContributeRequest contributeRequest = new ContributeRequest("title", "description", Collections.emptyList(), 1L,
			"title", 2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("문서가 존재하지 않습니다. documentId=1");
	}

	@Test
	@DisplayName("documentId에 해당하는 문서에 대한 수정요청이 이미 존재하는 경우")
	void votingContributeExists() {
		//given
		ContributeRequest contributeRequest = new ContributeRequest("title", "description", Collections.emptyList(), 1L,
			"title", 2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(true);

		//then
		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("이미 해당 문서에 대한 수정요청이 존재합니다. documentId=1");
	}

	@Test
	@DisplayName("documentId에 해당하는 문서에 대한 토론이 진행중인 경우")
	void debating() {
		//given
		ContributeRequest contributeRequest = new ContributeRequest("title", "description",
			Collections.emptyList(), 1L,
			"title", 2L, null);
		Long loginMemberId = 2L;
		Debate debate = debate(3L, null, DebateStatus.OPEN, LocalDateTime.now(), 1);

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(debate));

		//then
		assertThat(debate.isOnDebate()).isTrue();
		assertThatThrownBy(() -> contributeRequestValidator.validate(contributeRequest, loginMemberId))
			.isInstanceOf(BaseException.class).hasMessage("해당 문서에 대한 토론이 진행중입니다. debateId=" + debate.getId());
	}

	@Test
	@DisplayName("토론 종료 후 수정요청 대기중에 다른 토론에서 수정요청을 하려는 경우")
	void debatePendingRequestOtherDebate() {
		//given
		Long loginMemberId = 2L;
		Debate pendingDebate = debate(3L, null, DebateStatus.CLOSED,
			LocalDateTime.now(), 1);
		Debate noPendingDebate = debate(4L, null, DebateStatus.CLOSED,
			LocalDateTime.now().minusDays(1L), 1);

		ContributeRequest contributeRequest = new ContributeRequest("title", "description",
			Collections.emptyList(), 1L,
			"title", 2L, noPendingDebate.getId());

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(pendingDebate));

		//then
		assertThat(pendingDebate.isPendingForContribute()).isTrue();
		assertThat(pendingDebate.getId()).isNotEqualTo(contributeRequest.getRelatedDebateId());
		assertThatThrownBy(() -> contributeRequestValidator.validate(contributeRequest, loginMemberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("최근 종료된 토론 참여자를 위한 수정요청 대기중입니다. debateId=" + pendingDebate.getId());
	}

	@Test
	@DisplayName("토론 종료 후 수정요청 대기중에 토론에 참여하지 않았던 회원이 수정요청을 하려는 경우")
	void debatePendingRequestFromNoCommenter() {
		//given
		Long loginMemberId = 2L;
		Debate pendingDebate = debate(3L, null, DebateStatus.CLOSED,
			LocalDateTime.now(), 1);

		ContributeRequest contributeRequest = new ContributeRequest("title", "description",
			Collections.emptyList(), 1L,
			"title", 2L, pendingDebate.getId());

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(debateRepository.findLatestDebateByDocumentId(1L)).thenReturn(Optional.of(pendingDebate));

		//then
		assertThat(pendingDebate.isPendingForContribute()).isTrue();
		assertThat(pendingDebate.getId()).isEqualTo(contributeRequest.getRelatedDebateId());
		assertThat(pendingDebate.hasPermissionToWriteDrivenContribute(loginMemberId)).isFalse();

		assertThatThrownBy(() -> contributeRequestValidator.validate(contributeRequest, loginMemberId))
			.isInstanceOf(BaseException.class)
			.hasMessage("최근 종료된 토론 참여자를 위한 수정요청 대기중입니다. debateId=" + pendingDebate.getId());
	}

	@Test
	@DisplayName("변경할 title이 중복되는 경우 - 이미 존재하는 문서 제목")
	void duplicateDocumentTitle() {
		Document targetDocument = document(3L, member(1L, "pete"), "newTitle", 1L, null);

		ContributeRequest contributeRequest = new ContributeRequest("title", "description", Collections.emptyList(), 1L,
			"newTitle", 2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(sectionRepository.findSectionIdByVersion(any(), any())).thenReturn(Collections.emptyList());
		when(documentContentRepository.findByTitle("newTitle")).thenReturn(Optional.of(targetDocument));

		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("이미 해당 제목을 가진 문서가 존재합니다. title=newTitle");

	}

	@Test
	@DisplayName("변경할 title이 중복되는 경우 - 이미 변경요청중인 문서 제목")
	void duplicateDocumentTitle2() {
		ContributeRequest contributeRequest = new ContributeRequest("title", "description", Collections.emptyList(), 1L,
			"newTitle", 2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(sectionRepository.findSectionIdByVersion(any(), any())).thenReturn(Collections.emptyList());
		when(documentContentRepository.findByTitle("newTitle")).thenReturn(Optional.empty());
		when(contributeRepository.existsDuplicateRequestedDocumentTitle("newTitle")).thenReturn(true);

		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("해당 제목으로 변경을 요청중인 수정요청이 이미 존재합니다. title=newTitle");
	}

	@Test
	@DisplayName("amendments의 sectionId가 document에 존재하지 않는 경우")
	void noSectionInDocument() {

		AmendmentRequest a1 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);
		ContributeRequest contributeRequest = new ContributeRequest("title", "description", List.of(a1), 1L, "title",
			2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(sectionRepository.findSectionIdByVersion(any(), any())).thenReturn(List.of(2L));

		//then
		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("해당 문서에 존재하지 않는 섹션을 수정하려고 합니다. sectionId=1");
	}

	@Test
	@DisplayName("amendmentRequest의 creatingOrder가 중복되는 경우")
	void duplicateCreatingOrder() {

		AmendmentRequest a1 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);
		AmendmentRequest a2 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);
		ContributeRequest contributeRequest = new ContributeRequest("title", "description", List.of(a1, a2), 1L,
			"title", 2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(sectionRepository.findSectionIdByVersion(any(), any())).thenReturn(List.of(1L, 2L));

		//then
		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("중복된 생성 순서가 존재합니다. sectionId=1");
	}

	@Test
	@DisplayName("amendmentRequest의 creatingOrder가 순차적이지 않은 경우")
	void noSequentialOrder() {

		AmendmentRequest a1 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 3);
		AmendmentRequest a2 = new AmendmentRequest(1L, AmendmentType.CREATE, Heading.H2, "title",
			"content", 1);
		ContributeRequest contributeRequest = new ContributeRequest("title", "description", List.of(a1, a2), 1L,
			"title", 2L, null);
		Long loginMemberId = 2L;

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(mock(Document.class)));
		when(contributeRepository.existsByDocumentAndStatus(any(), any())).thenReturn(false);
		when(sectionRepository.findSectionIdByVersion(any(), any())).thenReturn(List.of(1L, 2L));

		//then
		assertThatThrownBy(
			() -> contributeRequestValidator.validate(contributeRequest, loginMemberId)
		).isInstanceOf(BaseException.class).hasMessage("생성 순서가 순차적이지 않습니다.");
	}
}