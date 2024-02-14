package goorm.eagle7.stelligence.domain.document;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.contribute.ContributeRepository;
import goorm.eagle7.stelligence.domain.document.content.DocumentContentRepository;
import goorm.eagle7.stelligence.domain.document.dto.DocumentCreateRequest;
import lombok.RequiredArgsConstructor;

/**
 * DocumentCreateRequest의 유효성을 검증하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
public class DocumentRequestValidator {

	private final DocumentContentRepository documentContentRepository;
	private final ContributeRepository contributeRepository;

	/**
	 * 문서 생성 요청의 유효성을 검증합니다.
	 * <p>검증 실패 케이스
	 * <ul>
	 *     <li>제목이 null이거나 빈값인 경우</li>
	 *     <li>내용이 null이거나 빈값인 경우</li>
	 *     <li>제목이 이미 존재하는 경우</li>
	 *     <li>수정하고자 하는 제목에 대해 다른 수정요청이 해당 제목으로 변경을 요청중인 경우</li>
	 * </ul>
	 * </p>
	 * @throws BaseException 유효하지 않은 요청일 경우
	 * @param request 수정요청 DTO
	 */
	@Transactional(readOnly = true)
	public void validate(DocumentCreateRequest request) {

		// 생성하려는 제목이 기존 내용과 중복될 여지가 있는가
		if (documentContentRepository.existsByTitle(request.getTitle())) {
			throw new BaseException("이미 존재하는 제목입니다.");
		}

		if (contributeRepository.existsDuplicateRequestedDocumentTitle(request.getTitle())) {
			throw new BaseException("이미 해당 제목으로 변경중인 수정요청이 존재합니다.");
		}

	}
}
