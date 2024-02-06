package goorm.eagle7.stelligence.domain.document.content;

import static goorm.eagle7.stelligence.config.mockdata.TestFixtureGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.domain.document.content.model.Document;

/**
 * 과거 통합테스트 시절의 전유물 때문에 read와 create 테스트로 분리되어있던 상황이었습니다.
 * changeTitle을 테스트하려니 위 두 클래스 중 어떤 곳에도 포함되어있지 않다는 사실을 깨달았습니다.
 * 추후 DocumentContentService 측 테스트 코드들을 통합할 예정입니다.
 */
@ExtendWith(MockitoExtension.class)
class DocumentContentServiceTest {
	@Mock
	DocumentContentRepository documentContentRepository;
	@InjectMocks
	DocumentContentService documentContentService;

	@Test
	@DisplayName("문서 이름 변경 테스트")
	void changeTitle() {
		Document document = document(1L, member(1L, "author1"), "title1", 1L);

		//when
		when(documentContentRepository.findById(1L)).thenReturn(java.util.Optional.of(document));
		documentContentService.changeTitle(1L, "changedTitle");

		//then
		assertThat(document.getTitle()).isEqualTo("changedTitle");

	}
}