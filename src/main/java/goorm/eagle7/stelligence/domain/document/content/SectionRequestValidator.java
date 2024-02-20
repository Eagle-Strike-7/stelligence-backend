package goorm.eagle7.stelligence.domain.document.content;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import goorm.eagle7.stelligence.api.exception.BaseException;
import goorm.eagle7.stelligence.domain.document.content.dto.SectionRequest;
import goorm.eagle7.stelligence.domain.section.model.Heading;
import lombok.RequiredArgsConstructor;

/**
 * SectionRequest의 유효성을 검증합니다.
 */
@Component
@RequiredArgsConstructor
public class SectionRequestValidator {

	private static final int MAX_TITLE_LENGTH = 100;

	public void validate(List<SectionRequest> sectionRequests) {
		for (SectionRequest req : sectionRequests) {
			validateHeading(req.getHeading());
			validateTitle(req.getTitle());
			validateContent(req.getContent());
		}
	}

	private void validateHeading(Heading heading) {
		if (heading == null) {
			throw new BaseException("Heading이 존재하지 않습니다.");
		}
	}

	private void validateTitle(String title) {
		if (!StringUtils.hasText(title) || title.length() > MAX_TITLE_LENGTH) {
			throw new BaseException("제목은 빈 값이면 안되며, 100자 이하로 입력해주세요.");
		}
	}

	private void validateContent(String content) {
		if (content != null && content.length() > 65535) {
			throw new BaseException("Content는 65535자 이하로 입력해주세요.");
		}
	}
}
