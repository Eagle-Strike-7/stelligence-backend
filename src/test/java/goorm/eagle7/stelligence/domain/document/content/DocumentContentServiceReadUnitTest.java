package goorm.eagle7.stelligence.domain.document.content;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.debate.repository.DebateRepository;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentResponse;
import goorm.eagle7.stelligence.domain.document.content.dto.DocumentSimpleResponse;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import goorm.eagle7.stelligence.domain.section.model.Section;

@ExtendWith(MockitoExtension.class)
class DocumentContentServiceReadUnitTest {

	@Mock
	DocumentContentRepository documentContentRepository;

	@Mock
	SectionRepository sectionRepository;

	@Mock
	ContributeRepository contributeRepository;

	@Mock
	DebateRepository debateRepository;

	@InjectMocks
	DocumentContentService documentContentService;

	@Test
	@DisplayName("문서 조회 - 최신버전")
	void getLatestDocumentSuccess() {
		//given
		Document document = document(1L, member(1L, "hello"), "title11", 3L);

		when(documentContentRepository.findById(1L))
			.thenReturn(Optional.of(document));

		//when
		DocumentResponse documentResponse = documentContentService.getDocument(1L);

		//then

		//최신버전을 찾아 호출해야한다.
		//원래는 getDocument(1L, 3L)을 검증하는게 맞지만, documentContentService는 모킹 대상이 아니므로 오류가 발생
		//따라서 이후 호출되는 findByVersion을 통해 검증함
		verify(sectionRepository).findByVersion(document, 3L);

		//최신버전의 문서를 조회했으므로, 현재 버전은 3이어야 한다.
		assertThat(documentResponse.getCurrentRevision()).isEqualTo(3L);
	}

	@Test
	@DisplayName("문서 조회 - 최신버전 - 문서가 없는 경우")
	void getLatestDocumentFailNoDocument() {
		//given
		when(documentContentRepository.findById(1L))
			.thenReturn(Optional.empty());

		//when
		//then
		assertThatThrownBy(() -> documentContentService.getDocument(1L))
			.isInstanceOf(BaseException.class)
			.hasMessage("문서가 존재하지 않습니다. 문서 ID : 1");
	}

	@Test
	@DisplayName("문서 조회 - 특정버전")
	void getDocumentByVersionSuccess() {

		//given
		Document document = document(1L, member(1L, "hello"), "title11", 4L);

		Section s1 = section(1L, 1L, document, Heading.H1, "title1", "content1", 1);
		Section s2 = section(2L, 2L, document, Heading.H2, "title3", "content3", 3);
		Section s3 = section(3L, 3L, document, Heading.H3, "title2", "content2", 2);

		//when
		when(documentContentRepository.findById(1L))
			.thenReturn(Optional.of(document));

		when(sectionRepository.findByVersion(document, 3L))
			.thenReturn(List.of(s1, s2, s3));

		DocumentResponse documentResponse = documentContentService.getDocument(1L, 3L);

		//assert
		assertThat(documentResponse.getDocumentId()).isEqualTo(1L);

		assertThat(documentResponse.getSections()).hasSize(3);

		//section의 순서가 order를 기준으로 오름차순 정렬되어야 한다.
		assertThat(documentResponse.getSections().get(0).getSectionId()).isEqualTo(1L); //order 1
		assertThat(documentResponse.getSections().get(1).getSectionId()).isEqualTo(3L); //order 2
		assertThat(documentResponse.getSections().get(2).getSectionId()).isEqualTo(2L); //order 3

		assertThat(documentResponse.getCurrentRevision()).isEqualTo(3L);
		assertThat(documentResponse.getLatestRevision()).isEqualTo(4L);
	}

	@Test
	@DisplayName("문서 조회 - 특정버전 - 문서가 없는 경우")
	void getDocumentByVersionFailNoDocument() {
		//given
		when(documentContentRepository.findById(1L))
			.thenReturn(Optional.empty());

		//when
		//then
		assertThatThrownBy(() -> documentContentService.getDocument(1L, 3L))
			.isInstanceOf(BaseException.class)
			.hasMessage("문서가 존재하지 않습니다. 문서 ID : 1");
	}

	@Test
	@DisplayName("문서 조회 - 특정버전 - 현재 버전보다 높은 버전을 조회하는 경우")
	void getDocumentByVersionFailVersionTooHigh() {
		//given
		Document document = document(1L, member(1L, "hello"), "title11", 3L);

		when(documentContentRepository.findById(1L))
			.thenReturn(Optional.of(document));

		//when
		//then
		assertThatThrownBy(() -> documentContentService.getDocument(1L, 4L))
			.isInstanceOf(BaseException.class)
			.hasMessage("존재하지 않는 버전입니다. 버전 : 4");
	}

	@Test
	@DisplayName("특정 내용을 담고 있는 문서의 ID 목록 조회")
	void getDocumentIdWhichContainsKeywordInLatestVersionSuccess() {
		//given
		when(documentContentRepository.findDocumentIdWhichContainsKeywordInLatestVersion("keyword"))
			.thenReturn(List.of(1L, 2L, 3L));

		//when
		List<Long> documentIds = documentContentService.findDocumentWhichContainsKeyword("keyword");
		//then
		assertThat(documentIds).hasSize(3).containsExactly(1L, 2L, 3L);
	}

	@Test
	@DisplayName("문서 조회 - 상위 문서 존재")
	void parentDocumentTest() {
		//given
		Document parentDocument = document(2L, member(2L, "world"), "parentTitle", 1L);
		Document document = document(1L, member(1L, "hello"), "title11", 1L, parentDocument);

		Section s1 = section(1L, 1L, document, Heading.H1, "title1", "content1", 1);

		//when
		when(documentContentRepository.findById(1L)).thenReturn(Optional.of(document));
		when(sectionRepository.findByVersion(document, 1L)).thenReturn(List.of(s1));

		DocumentResponse documentResponse = documentContentService.getDocument(1L);

		//then
		assertThat(documentResponse.getParentDocumentId()).isEqualTo(2L);
		assertThat(documentResponse.getParentDocumentTitle()).isEqualTo("parentTitle");
	}

	@Test
	@DisplayName("문서 제목으로 검색 - 존재하는 제목")
	void getExistDocumentByTitle() {
		//given
		final String searchTitle = "title1";
		Document document = document(1L, member(1L, "hello"), searchTitle, 1L, null);

		//when
		when(documentContentRepository.findByTitle(searchTitle)).thenReturn(Optional.of(document));

		DocumentSimpleResponse documentSimpleResponse = documentContentService.getDocumentByTitle(searchTitle);

		//then
		assertThat(documentSimpleResponse.getTitle()).isEqualTo(searchTitle);
		assertThat(documentSimpleResponse.getDocumentId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("문서 제목으로 검색 - 존재하지 않는 제목")
	void getNonExistDocumentByTitle() {
		//given
		final String searchTitle = "nonExistTitle";

		//when
		when(documentContentRepository.findByTitle(searchTitle)).thenReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> documentContentService.getDocumentByTitle(searchTitle))
			.isInstanceOf(BaseException.class)
			.hasMessage("해당 제목을 갖는 문서가 존재하지 않습니다. 제목: " + searchTitle);
	}
}
