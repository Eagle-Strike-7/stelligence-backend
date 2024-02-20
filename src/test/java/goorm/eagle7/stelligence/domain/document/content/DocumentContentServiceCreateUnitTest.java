package goorm.eagle7.stelligence.domain.document.content;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.html.PolicyFactory;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;
import goorm.eagle7.stelligence.config.MockSectionIdGenerator;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.content.parser.DocumentParser;
import goorm.eagle7.stelligence.domain.member.model.Member;
import goorm.eagle7.stelligence.domain.section.SectionRepository;
import goorm.eagle7.stelligence.domain.section.model.Heading;

/**
 * DocumentService를 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
class DocumentContentServiceCreateUnitTest {

	@Mock
	DocumentContentRepository documentContentRepository;

	@Mock
	SectionRepository sectionRepository;

	@Mock
	SectionIdGenerator sectionIdGenerator = new MockSectionIdGenerator();

	@Mock
	DocumentParser documentParser;

	@Mock
	PolicyFactory policyFactory;

	@Mock
	SectionRequestValidator sectionRequestValidator;

	@InjectMocks
	DocumentContentService documentContentService;

	@Test
	@DisplayName("문서 생성 - 부모 문서 없는 경우 - 성공")
	void createDocumentNoParentSuccess() {
		//given
		String title = "title";
		String rawContent = "# title1\ntestRawContent";
		String nickname = "nickname";

		Member author = member(1L, nickname);

		//문서 파싱 결과
		List<SectionRequest> sectionRequests = new ArrayList<>();
		sectionRequests.add(new SectionRequest(Heading.H1, "title1", "testRawContent\n"));
		sectionRequests.add(new SectionRequest(Heading.H2, "title2", "content2 line 1\ncontent2 line 2\n"));

		//when
		when(policyFactory.sanitize(rawContent)).thenReturn(rawContent);
		when(documentParser.parse(rawContent)).thenReturn(sectionRequests);
		Document document = documentContentService.createDocument(title, rawContent, null, author);

		//then

		//각각의 모듈이 정상적으로 호출되었는지 확인
		verify(documentContentRepository, times(1)).save(any());
		verify(sectionRepository, times(2)).save(any());
		verify(sectionIdGenerator, times(2)).getAndIncrementSectionId();
		verify(documentParser, times(1)).parse(rawContent);

		//document의 값이 정상적으로 들어갔는지 확인
		assertThat(document.getTitle()).isEqualTo(title);
		assertThat(document.getAuthor().getNickname()).isEqualTo(nickname);
		assertThat(document.getSections()).hasSize(2);
		//부모 문서가 없으므로 null
		assertThat(document.getParentDocument()).isNull();

		//section의 값이 정상적으로 들어갔는지 확인
		assertThat(document.getSections().get(0).getHeading()).isEqualTo(Heading.H1);
		assertThat(document.getSections().get(1).getTitle()).isEqualTo("title2");
		assertThat(document.getSections().get(0).getOrder()).isEqualTo(1);
		assertThat(document.getSections().get(1).getOrder()).isEqualTo(2);
	}

	@Test
	@DisplayName("문서 생성 - 부모 문서 있는 경우 - 성공")
	void createDocumentWithParentSuccess() {
		//given
		String title = "title";
		String rawContent = "# title1\ntestRawContent";
		String nickname = "nickname";

		Member author = member(1L, nickname);
		Document parent = document(2L, author, "title", 1L);

		//문서 파싱 결과
		List<SectionRequest> sectionRequests = new ArrayList<>();
		sectionRequests.add(new SectionRequest(Heading.H1, "title1", "testRawContent\n"));
		sectionRequests.add(new SectionRequest(Heading.H2, "title2", "content2 line 1\ncontent2 line 2\n"));

		//when
		when(policyFactory.sanitize(rawContent)).thenReturn(rawContent);
		when(documentParser.parse(rawContent)).thenReturn(sectionRequests);
		when(documentContentRepository.findById(2L)).thenReturn(
			Optional.of(parent));
		Document document = documentContentService.createDocument(title, rawContent, 2L, author);

		//then
		//부모 문서가 없으므로 null
		assertThat(document.getParentDocument()).isEqualTo(parent);
	}

}