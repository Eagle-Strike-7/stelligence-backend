package goorm.eagle7.stelligence.domain.document;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;

@ExtendWith(MockitoExtension.class)
class DocumentRequestValidatorTest {

	@Mock
	DocumentContentRepository documentContentRepository;

	@Mock
	ContributeRepository contributeRepository;

	@InjectMocks
	DocumentRequestValidator documentRequestValidator;

	@Test
	@DisplayName("제목이 null이거나 빈값인 경우")
	void validateThrowsTitle() {
		//given
		DocumentCreateRequest request1 = DocumentCreateRequest.of(null, null, "content");
		DocumentCreateRequest request2 = DocumentCreateRequest.of("  ", null, "content");

		//then
		assertThatThrownBy(
			() -> documentRequestValidator.validate(request1))
			.isInstanceOf(BaseException.class)
			.hasMessage("문서의 제목이 비어있습니다.");

		assertThatThrownBy(
			() -> documentRequestValidator.validate(request2))
			.isInstanceOf(BaseException.class)
			.hasMessage("문서의 제목이 비어있습니다.");
	}

	@Test
	@DisplayName("내용이 null이거나 빈값인 경우")
	void validateThrowsContent() {
		//given
		DocumentCreateRequest request1 = DocumentCreateRequest.of("title", null, null);
		DocumentCreateRequest request2 = DocumentCreateRequest.of("title", null, "           ");

		//then
		assertThatThrownBy(
			() -> documentRequestValidator.validate(request1))
			.isInstanceOf(BaseException.class)
			.hasMessage("문서의 내용이 비어있습니다.");

		assertThatThrownBy(
			() -> documentRequestValidator.validate(request2))
			.isInstanceOf(BaseException.class)
			.hasMessage("문서의 내용이 비어있습니다.");
	}

	@Test
	@DisplayName("제목이 이미 존재하는 경우")
	void validateThrowsDuplicateTitle() {
		//given
		DocumentCreateRequest request = DocumentCreateRequest.of("title", null, "content");
		when(documentContentRepository.existsByTitle("title")).thenReturn(true);

		//then
		assertThatThrownBy(
			() -> documentRequestValidator.validate(request))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 존재하는 제목입니다.");
	}

	@Test
	@DisplayName("수정하고자 하는 제목에 대해 다른 수정요청이 해당 제목으로 변경을 요청중인 경우")
	void validateThrowsDuplicateRequestedDocumentTitle() {
		//given
		DocumentCreateRequest request = DocumentCreateRequest.of("title", null, "content");
		when(documentContentRepository.existsByTitle("title")).thenReturn(false);
		when(contributeRepository.existsDuplicateRequestedDocumentTitle("title")).thenReturn(true);

		//then
		assertThatThrownBy(
			() -> documentRequestValidator.validate(request))
			.isInstanceOf(BaseException.class)
			.hasMessage("이미 해당 제목으로 변경중인 수정요청이 존재합니다.");
	}
}