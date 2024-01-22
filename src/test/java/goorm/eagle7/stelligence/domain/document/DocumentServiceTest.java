package goorm.eagle7.stelligence.domain.document;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.document.content.DocumentContentService;
import goorm.eagle7.stelligence.domain.document.content.model.Document;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import goorm.eagle7.stelligence.domain.document.graph.DocumentGraphService;
import goorm.eagle7.stelligence.domain.member.MemberRepository;
import goorm.eagle7.stelligence.domain.member.model.Member;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private DocumentContentService documentContentService;

	@Mock
	private DocumentGraphService documentGraphService;

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
		when(documentContentService.createDocument("testTitle", "testSectionContent", author))
			.thenReturn(document);

		//when
		documentService.createDocument(
			documentCreateRequest,
			author.getId()
		);

		//then
		//memberRepository, documentContentService, documentGraphService가 각각 한 번씩 호출되어야 합니다.
		verify(memberRepository, times(1)).findById(author.getId());
		verify(documentContentService, times(1)).createDocument("testTitle", "testSectionContent", author);
		verify(documentGraphService, times(1)).createDocumentNode(document);

		//기여한 글 개수가 올라야 합니다.
		assertThat(author.getContributes()).isEqualTo(1);
	}
}