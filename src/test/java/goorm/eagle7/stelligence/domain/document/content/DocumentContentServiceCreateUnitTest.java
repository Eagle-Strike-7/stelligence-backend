package goorm.eagle7.stelligence.domain.document.content;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

	@InjectMocks
	DocumentContentService documentContentService;

	@Test
	@DisplayName("문서 생성 - 성공")
	void createDocumentSuccess() {
		//given
		String title = "title";
		String rawContent = "# title1\ntestRawContent";
		String nickname = "nickname";

		Member author = member(1L, nickname);

		//문서 파싱 결과
		List<SectionRequest> sectionRequests = new ArrayList<>();
		sectionRequests.add(new SectionRequest(Heading.H1, "title1", "testRawContent\n"));
		sectionRequests.add(new SectionRequest(Heading.H2, "title2", "content2 line 1\ncontent2 line 2\n"));

		//문서 파싱 결과를 반환하도록 설정
		when(documentParser.parse(rawContent)).thenReturn(sectionRequests);

		//when
		Document document = documentContentService.createDocument(title, rawContent, author);

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

		//section의 값이 정상적으로 들어갔는지 확인
		assertThat(document.getSections().get(0).getHeading()).isEqualTo(Heading.H1);
		assertThat(document.getSections().get(1).getTitle()).isEqualTo("title2");
		assertThat(document.getSections().get(0).getOrder()).isEqualTo(1);
		assertThat(document.getSections().get(1).getOrder()).isEqualTo(2);
	}

}